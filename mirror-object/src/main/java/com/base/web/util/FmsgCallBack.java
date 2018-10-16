package com.base.web.util;

import com.base.web.bean.*;
import com.base.web.bean.po.GenericPo;
import com.base.web.bean.po.resource.Resources;
import com.base.web.service.*;
import com.base.web.service.resource.FileService;
import com.base.web.service.resource.ResourcesService;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.nio.sctp.Association;
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

    @Autowired
    private AssociationBlickListService associationBlickListService;


    /**
     * 检索黑名单线程池
     */
    private static final ExecutorService RETRIEVE_BLACKLIST_POOL = new ThreadPoolExecutor(3, 250,
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
    private static final ExecutorService FMSGCALLBACK_POOL = new ThreadPoolExecutor(6, 250,
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
    private static final ExecutorService ENGINE_POOL = new ThreadPoolExecutor(3, 250,
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
            FMSGCALLBACK_POOL.execute(new FmsgCallBackThread(pAlarmInfo));
        }
    }


    /**
     * 抓拍保存图片
     */
    private class FmsgCallBackThread implements Runnable {

        private HCNetSDK.NET_VCA_FACESNAP_RESULT pAlarmInfo;


        public FmsgCallBackThread(HCNetSDK.NET_VCA_FACESNAP_RESULT pAlarmInfo) {
            this.pAlarmInfo = pAlarmInfo;

        }

        @Override
        public void run() {
            //获取摄像头抓拍的时间
            int time = pAlarmInfo.dwAbsTime;
            Date date = new Date((time >> 26) + 100, ((time >> 22) & 15) - 1, (time >> 17) & 31,
                    (time >> 12) & 31, (time >> 6) & 63, time & 63);
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
                //读取图片到byte[]
                ByteBuffer buffers = pAlarmInfo.pBuffer2.getByteBuffer(0, pAlarmInfo.dwBackgroundPicLen);
                byte[] bytes = new byte[pAlarmInfo.dwBackgroundPicLen];
                buffers.rewind();
                buffers.get(bytes);
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
                    String ip = new String(pAlarmInfo.struDevInfo.struDevIP.sIpV4, StandardCharsets.UTF_8)
                            .split("\0", 2)[0];
                    Long cameraId = cameraService.createQuery().where(Camera.Property.IP, ip)
                            .and(Camera.Property.PORT, pAlarmInfo.struDevInfo.wPort).single().getId();
                    File newFile = new File(absPath.concat("/").concat(md5));
                    resources = new Resources();
                    resources.setType("file");
                    resources.setSize(pAlarmInfo.dwBackgroundPicLen);
                    resources.setName(md5);
                    resources.setPath(filePath);
                    resources.setMd5(md5);
                    resources.setCreateTime(date);
                    Long resourceId = resourcesService.insert(resources);
                    ENGINE_POOL.execute(new Engine(oldFile, newFile, cameraId, resourceId, date));
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
    private class Engine implements Runnable {

        private File oldFile;
        private File newFile;
        private Long resourceId;
        private Long cameraId;
        private Date date;


        public Engine(File oldFile, File newFile, Long cameraId, Long resourceId, Date date) {

            this.oldFile = oldFile;
            this.newFile = newFile;
            this.cameraId = cameraId;
            this.resourceId = resourceId;
            this.date = date;
        }

        @Override
        public void run() {
            byte[][] bytes = FaceFeatureUtil.ENGINEMAPS.get(cameraId).returnFaceFeature(oldFile);
            if (bytes != null && bytes.length > 0) {
                oldFile.renameTo(newFile);
                FaceImage faceImage = new FaceImage();
                faceImage.setDeviceId(cameraId);
                faceImage.setCreateTime(date);
                faceImage.setResourceId(resourceId);
                Long faceImageId = faceImageService.insert(faceImage);
                RETRIEVE_BLACKLIST_POOL.execute(new RetrieveBlacklistThread(bytes, faceImageId, resourceId));
            } else {
                oldFile.delete();
            }
        }
    }

    /**
     * 检索黑名单
     */
    private class RetrieveBlacklistThread implements Runnable {

        private byte[][] bytes;

        private Long faceImageId;
        private Long resourceId;

        public RetrieveBlacklistThread(byte[][] bytes, Long faceImageId, Long resourceId) {
            this.bytes = bytes;
            this.faceImageId = faceImageId;
            this.resourceId = resourceId;
        }

        @Override
        public void run() {
            List<BlackList> list = blackListService.select();
            FaceFeature faceFeature = new FaceFeature();
            AssociationBlickListDO associationBlickListDO = new AssociationBlickListDO();
            float similarity;
            //遍历图片所有特征值
            for (int j =  0; j < bytes.length; j++) {
                //遍历所有黑名单
                for (int i = 0 ; i < list.size(); ) {
                    try {
                        similarity = FaceFeatureUtil.ENGINEMAPS.get(0L).compareFaceSimilarity(bytes[j], list.get(i).getFaceFeature());
                        if (similarity - 0.4 > 0) {
                            associationBlickListDO.setBlackListId(list.get(i).getId());
                            associationBlickListDO.setFaceImageId(faceImageId);
                            associationBlickListDO.setSimilarity((int) (similarity * 100));
                            associationBlickListDO.setId(GenericPo.createUID());
                            associationBlickListService.insert(associationBlickListDO);
                            list.remove(i);
                            continue;
                        }
                        i++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                faceFeature.setResourceId(resourceId);
                faceFeature.setFaceFeature(bytes[j]);
                faceFeatureService.insert(faceFeature);
            }
        }
    }

}