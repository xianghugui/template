package com.base.web.util;

import com.base.web.bean.Camera;
import com.base.web.bean.FaceImage;
import com.base.web.bean.po.resource.Resources;
import com.base.web.service.CameraService;
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

import java.beans.Transient;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Date;

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

    /**
     * 检测到人脸保存图片
     * @param lCommand
     * @param pAlarmer
     * @param pAlarmInfo
     * @param dwBufLen
     * @param pUser
     * @throws IOException
     */
    @Override
    @Transient
    public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.NET_VCA_FACESNAP_RESULT pAlarmInfo, int dwBufLen, Pointer pUser) {
        if(pAlarmInfo.dwFacePicLen > 0)
        {
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
            Resources resources;
            String md5 = null;
            try {
                //保存图片
                fout = new FileOutputStream(fileAbsName);
                ByteBuffer buffers = pAlarmInfo.pBuffer1.getPointer().getByteBuffer(0, pAlarmInfo.dwFacePicLen);
                byte [] bytes = new byte[pAlarmInfo.dwFacePicLen];
                buffers.rewind();
                buffers.get(bytes);
                fout.write(bytes);
                fout.flush();
                fout.close();
                //根据MD5值命名
                File newFile = new File(fileAbsName);
                FileInputStream inputStream = new FileInputStream(newFile);
                md5 = DigestUtils.md5Hex(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }
                resources = resourcesService.selectByMd5(md5);
                if (resources != null) {
                    newFile.delete();
                    return;
                } else {
                    newFile.renameTo(new File(absPath.concat("/").concat(md5)));
                }
            }catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Date date = new Date();
            resources = new Resources();
            resources.setType("file");
            resources.setSize(pAlarmInfo.dwFacePicLen);
            resources.setName(md5);
            resources.setPath(filePath);
            resources.setMd5(md5);
            resources.setCreateTime(date);
            Long id = resourcesService.insert(resources);
            FaceImage faceImage = new FaceImage();
            faceImage.setResourceId(id);
            String ip = new String(pAlarmInfo.struDevInfo.struDevIP.sIpV4).split("\0", 2)[0];
            short port = pAlarmInfo.struDevInfo.wPort;
            Camera camera = cameraService.createQuery().where(Camera.Property.IP, ip)
                    .and(Camera.Property.PORT, port).single();
            faceImage.setDeviceId(camera.getId());
            faceImage.setCreateTime(date);
            faceImageService.insert(faceImage);
        }

    }

}