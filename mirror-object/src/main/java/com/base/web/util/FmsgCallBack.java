package com.base.web.util;

import com.base.web.bean.BlackList;
import com.base.web.bean.Camera;
import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;
import com.base.web.bean.po.GenericPo;
import com.base.web.bean.po.resource.Resources;
import com.base.web.service.BlackListService;
import com.base.web.service.CameraService;
import com.base.web.service.FaceFeatureService;
import com.base.web.service.FaceImageService;
import com.base.web.service.resource.FileService;
import com.base.web.service.resource.ResourcesService;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.apache.commons.codec.digest.DigestUtils;
import org.hsweb.commons.DateTimeUtils;
import org.hsweb.commons.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 海康报警回调
 */
@Service
public class FmsgCallBack implements HCNetSDK.FMSGCallBack {

    @Autowired
    private FileService fileService;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private FaceImageService faceImageService;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private FaceFeatureService faceFeatureService;

    @Autowired
    private BlackListService blackListService;

    /**
     * 检索黑名单线程池
     */
    private static final ExecutorService RETRIEVE_BLACKLIST_POOL = new ThreadPoolExecutor(0, 250,
            0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(),
            new ThreadFactory() {

                private final AtomicInteger mCount = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "RetrieveBlacklist_pool:" + mCount.getAndIncrement());
                }
            });


    /**
     * 回调函数线程池
     */
    private static final ExecutorService FMSGCALLBACK_POOL = new ThreadPoolExecutor(0, 250,
            0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(),
            new ThreadFactory() {

                private final AtomicInteger mCount = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "FmsgCallBack_pool:" + mCount.getAndIncrement());
                }
            });

    /**
     * 人脸识别引擎线程池
     */
    private static final ExecutorService ENGINE_POOL = new ThreadPoolExecutor(0, 250,
            0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(),
            new ThreadFactory() {

                private final AtomicInteger mCount = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "Engine_pool:" + mCount.getAndIncrement());
                }
            });


    /**
     * 检测到人脸保存图片
     *
     * @param lCommand
     * @param pAlarmer
     * @param pAlarmInfo
     * @param dwBufLen
     * @param pUser
     * @throws IOException
     */
    @Override
    public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.NET_VCA_FACESNAP_RESULT pAlarmInfo,
                       int dwBufLen, Pointer pUser) {
        if (pAlarmInfo.dwBackgroundPicLen > 0) {
            ByteBuffer buffers = pAlarmInfo.pBuffer2.getByteBuffer(0, pAlarmInfo.dwBackgroundPicLen);
            byte[] bytes = new byte[pAlarmInfo.dwBackgroundPicLen];
            buffers.rewind();
            buffers.get(bytes);
            int time = pAlarmInfo.dwAbsTime;
            Date date = new Date((time>>26) + 100,((time>>22) & 15) - 1,(time>>17) & 31,
                    (time>>12) & 31,(time>>6) & 63,time & 63);
            FMSGCALLBACK_POOL.execute(new FmsgCallBackThread(bytes, new String(pAlarmInfo.struDevInfo.struDevIP.sIpV4, StandardCharsets.UTF_8)
                    .split("\0", 2)[0], pAlarmInfo.struDevInfo.wPort, pAlarmInfo.dwBackgroundPicLen, date));
        }
    }

    @Transactional
    public void insert(Resources resources, FaceImage faceImage, Map<Integer, byte[]> map) {
        Long id = resourcesService.insert(resources);
        faceImage.setResourceId(id);
        faceImageService.insert(faceImage);
        for (byte[] faceFeature : map.values()) {
            FaceFeature faceFeature1 = new FaceFeature();
            faceFeature1.setId(GenericPo.createUID());
            faceFeature1.setFaceImageId(id);
            faceFeature1.setFaceFeature(faceFeature);
            faceFeatureService.insert(faceFeature1);
        }
    }


    /**
     * 抓拍保存图片
     */
    private class FmsgCallBackThread implements Runnable {

        private byte[] bytes;
        private String ip;
        private short port;
        private int picLen;
        private Date date;

        public FmsgCallBackThread(byte[] bytes, String ip, short port, int picLen, Date date) {
            this.bytes = bytes;
            this.ip = ip;
            this.port = port;
            this.picLen = picLen;
            this.date = date;
        }

        @Override
        public void run() {
            //创建临时文件
            String filePath = "/file/".concat(DateTimeUtils.format(date, DateTimeUtils.YEAR_MONTH_DAY));
            String absPath = fileService.getFileBasePath().concat(filePath);
            File path = new File(absPath);
            if (!path.exists()) {
                path.mkdirs();
            }
            String newName = MD5.encode(String.valueOf(System.nanoTime()));
            String fileAbsName = absPath.concat("/").concat(newName);
            FileOutputStream fout;
            try {
                //保存图片
                fout = new FileOutputStream(fileAbsName);
                fout.write(bytes);
                fout.close();
                //根据MD5值命名
                File oldFile = new File(fileAbsName);
                FileInputStream inputStream = new FileInputStream(oldFile);
                String md5 = DigestUtils.md5Hex(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }
                Resources resources = resourcesService.selectByMd5(md5);
                if (resources != null) {
                    oldFile.delete();
                    return;
                } else {
                    Camera camera = cameraService.createQuery().where(Camera.Property.IP, ip)
                            .and(Camera.Property.PORT, port).single();
                    File newFile = new File(absPath.concat("/").concat(md5));
                    resources = new Resources();
                    resources.setType("file");
                    resources.setSize(picLen);
                    resources.setName(md5);
                    resources.setPath(filePath);
                    resources.setMd5(md5);
                    resources.setCreateTime(date);
                    ENGINE_POOL.execute(new Engine(oldFile,newFile,camera, resources));
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }


    /**
     * 提取特征值
     */
    private class Engine implements Runnable{

        private File oldFile;
        private Resources resources;
        private File newFile;
        private Camera camera;


        public Engine(File oldFile, File newFile, Camera camera, Resources resources) {

            this.oldFile = oldFile;
            this.newFile = newFile;
            this.camera = camera;
            this.resources = resources;
        }

        @Override
        public void run() {
            Map<Integer, byte[]> map = FaceFeatureUtil.ENGINEMAPS.get(camera.getId()).returnFaceFeature(oldFile);
            if (map.size() > 0) {
                oldFile.renameTo(newFile);
                FaceImage faceImage = new FaceImage();
                faceImage.setDeviceId(camera.getId());
                faceImage.setBlacklistId(0L);
                faceImage.setCreateTime(resources.getCreateTime());
                //插入数据
                insert(resources, faceImage, map);
                RETRIEVE_BLACKLIST_POOL.execute(new RetrieveBlacklistThread(map.get(0), faceImage.getId()));
            } else {
                oldFile.delete();
            }
        }
    }

    /**
     * 检索黑名单
     */
    private class RetrieveBlacklistThread implements Runnable {

        private byte[] faceFeatureA;

        private Long faceImageId;

        public RetrieveBlacklistThread(byte[] faceFeatureA, Long faceImageId) {
            this.faceFeatureA = faceFeatureA;
            this.faceImageId = faceImageId;
        }

        @Override
        public void run() {
            List<BlackList> list = blackListService.select();
            for (BlackList blackList : list) {
                try {
                    float similarity = FaceFeatureUtil.ENGINEMAPS.get(0L).compareFaceSimilarity(faceFeatureA, blackList.getFaceFeature());
                    if (similarity - 0.4 > 0) {
                        FaceImage faceImage = new FaceImage();
                        faceImage.setId(faceImageId);
                        faceImage.setBlacklistId(blackList.getId());
                        faceImage.setSimilarity((int) (similarity*100));
                        faceImageService.update(faceImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}