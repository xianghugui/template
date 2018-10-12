package com.base.web.util;

import com.base.web.bean.Camera;
import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;
import com.base.web.bean.po.GenericPo;
import com.base.web.bean.po.resource.Resources;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.service.CameraService;
import com.base.web.service.FaceFeatureService;
import com.base.web.service.FaceImageService;
import com.base.web.service.resource.FileService;
import com.base.web.service.resource.ResourcesService;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import java.util.Date;
import java.util.HashMap;
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

//    @Autowired
//    private FaceFeatureUtil faceFeatureUtil;

    Map<Long,FaceFeatureUtil> maps = new HashMap<Long,FaceFeatureUtil>();

    public void setMaps(Map<Long, FaceFeatureUtil> maps) {
        this.maps = maps;
    }

    /**
     * 检测到人脸保存图片
     *
     * @param lCommand
     * @param pAlarmer
     * @param strFaceSnapInfo
     * @param dwBufLen
     * @param pUser
     * @throws IOException
     */
    @Override
    public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.NET_VCA_FACESNAP_RESULT strFaceSnapInfo, int dwBufLen, Pointer pUser) {
        Long start = System.currentTimeMillis();
        Date date = new Date();
        if (strFaceSnapInfo.dwBackgroundPicLen > 0) {
            //创建临时文件
            String filePath = "/file/".concat(DateTimeUtils.format(new Date(), DateTimeUtils.YEAR_MONTH_DAY));
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
                //将字节写入文件
                long offset = 0;
                ByteBuffer buffers = strFaceSnapInfo.pBuffer2.getByteBuffer(offset, strFaceSnapInfo.dwBackgroundPicLen);
                byte[] bytes = new byte[strFaceSnapInfo.dwBackgroundPicLen];
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
                    File newFile = new File(absPath.concat("/").concat(md5));
                    Map<Integer, byte[]> map = maps.get(pAlarmer.lUserID.longValue()).returnFaceFeature(oldFile);
                    if (map.size() > 0) {
                        oldFile.renameTo(newFile);
                        resources = new Resources();
                        resources.setType("file");
                        resources.setSize(strFaceSnapInfo.dwBackgroundPicLen);
                        resources.setName(md5);
                        resources.setPath(filePath);
                        resources.setMd5(md5);
                        resources.setCreateTime(date);
                        FaceImage faceImage = new FaceImage();
                        String ip = new String(strFaceSnapInfo.struDevInfo.struDevIP.sIpV4).split("\0", 2)[0];
                        short port = strFaceSnapInfo.struDevInfo.wPort;
                        Camera camera = cameraService.createQuery().where(Camera.Property.IP, ip)
                                .and(Camera.Property.PORT, port).single();
                        if (camera != null) {
                            faceImage.setDeviceId(camera.getId());
                        }
                        faceImage.setCreateTime(date);
                        //插入数据
                        insert(resources, faceImage, map);
                    } else {
                        oldFile.delete();
                    }
                    System.out.println((System.currentTimeMillis() - start) * 1.0 / 1000);
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

}