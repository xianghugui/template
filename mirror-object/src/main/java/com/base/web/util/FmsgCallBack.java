package com.base.web.util;

import com.base.web.bean.*;
import com.base.web.bean.po.GenericPo;
import com.base.web.bean.po.resource.Resources;
import com.base.web.service.*;
import com.base.web.service.resource.FileService;
import com.base.web.service.resource.ResourcesService;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.apache.commons.codec.digest.DigestUtils;
import org.hsweb.commons.DateTimeUtils;
import org.hsweb.commons.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
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
    private static final ExecutorService RETRIEVE_BLACKLIST_POOL = new ThreadPoolExecutor(0, 250,
            3L, TimeUnit.SECONDS, new LinkedBlockingQueue(1000),
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
            3L, TimeUnit.SECONDS, new LinkedBlockingQueue(1000),
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
            3L, TimeUnit.SECONDS, new LinkedBlockingQueue(1000),
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
                    Long cameraId = cameraService.createQuery().where(Camera.Property.IP, ip)
                            .and(Camera.Property.PORT, port).single().getId();
                    File newFile = new File(absPath.concat("/").concat(md5));
                    resources = new Resources();
                    resources.setType("file");
                    resources.setSize(picLen);
                    resources.setName(md5);
                    resources.setPath(filePath);
                    resources.setMd5(md5);
                    resources.setCreateTime(date);
                    ENGINE_POOL.execute(new Engine(oldFile, newFile, cameraId, resources));
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
        private Resources resources;
        private Long cameraId;

        public Engine(File oldFile, File newFile, Long cameraId, Resources resources) {

            this.oldFile = oldFile;
            this.newFile = newFile;
            this.cameraId = cameraId;
            this.resources = resources;
        }

        @Override
        public void run() {
            byte[][] bytes = FaceFeatureUtil.ENGINEMAPS.get(cameraId).returnFaceFeature(oldFile);
            if (bytes != null && bytes.length > 0) {
                oldFile.renameTo(newFile);
                RETRIEVE_BLACKLIST_POOL.execute(new RetrieveBlacklistThread(bytes, resources, cameraId));
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
        private Resources resources;
        private Long cameraId;

        public RetrieveBlacklistThread(byte[][] bytes, Resources resources, Long cameraId) {
            this.bytes = bytes;
            this.resources = resources;
            this.cameraId = cameraId;
        }

        @Override
        public void run() {
            //插入抓拍图片记录
            resources.setId(GenericPo.createUID());
            Long resourceId = resourcesService.insert(resources);
            FaceImage faceImage = new FaceImage();
            faceImage.setId(GenericPo.createUID());
            faceImage.setDeviceId(cameraId);
            faceImage.setCreateTime(resources.getCreateTime());
            faceImage.setResourceId(resourceId);
            Long faceImageId = faceImageService.insert(faceImage);
            //检索黑名单
            List<BlackList> list = blackListService.createQuery().where(BlackList.Property.status, 0).list();
            FaceFeature faceFeature = new FaceFeature();
            AssociationBlickListDO associationBlickListDO = new AssociationBlickListDO();
            //遍历图片所有特征值
            for (int i =  0; i < bytes.length; i++) {
                if (bytes[i].length > 0) {
                    float maxSimilarity = 0F;
                    int blackListIndex = -1;
                    //遍历所有黑名单,获取匹配度最高的索引
                    for (int j = 0; j < list.size(); j++) {
                        try {
                            float similarity = FaceFeatureUtil.ENGINEMAPS.get(0L).compareFaceSimilarity(bytes[i], list.get(j).getFaceFeature());
                            if (similarity > maxSimilarity) {
                                maxSimilarity = similarity;
                                blackListIndex = j;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //相似度大于黑名单中设置的相似度值才插入数据
                    if (maxSimilarity > list.get(blackListIndex).getSimilarity() && blackListIndex > 0) {
                        associationBlickListDO.setId(GenericPo.createUID());
                        associationBlickListDO.setBlackListId(list.get(blackListIndex).getId());
                        associationBlickListDO.setFaceImageId(faceImageId);
                        associationBlickListDO.setSimilarity((int) (maxSimilarity * 100));
                        associationBlickListDO.setCreateTime(resources.getCreateTime());
                        associationBlickListService.insert(associationBlickListDO);
                    }
                    faceFeature.setId(GenericPo.createUID());
                    faceFeature.setResourceId(resourceId);
                    faceFeature.setFaceFeature(bytes[i]);
                    faceFeature.setCreateTime(resources.getCreateTime());
                    faceFeatureService.insert(faceFeature);
                }
            }
        }
    }

}