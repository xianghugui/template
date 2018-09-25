package com.base.web.util;

import com.base.web.bean.po.resource.Resources;
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
    public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.RECV_ALARM pAlarmInfo, int dwBufLen, Pointer pUser) throws IOException {
        if(pAlarmInfo.dwFacePicLen>0)
        {
            //文件存储的相对路径，以日期分隔，每天创建一个新的目录
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
                fout = new FileOutputStream(fileAbsName);
                //将字节写入文件
                ByteBuffer buffers = pAlarmInfo.pBuffer1.getPointer().getByteBuffer(0, pAlarmInfo.dwFacePicLen);
                byte [] bytes = new byte[pAlarmInfo.dwFacePicLen];
                buffers.rewind();
                buffers.get(bytes);
                fout.write(bytes);
                fout.close();
                File newFile = new File(fileAbsName);
                FileInputStream inputStream = new FileInputStream(newFile);
                String md5 = DigestUtils.md5Hex(inputStream);
                Resources resources = resourcesService.selectByMd5(md5);
                if (resources != null) {
                    newFile.delete();//文件已存在则删除临时文件不做处理
                    return;
                } else {
                    System.out.println(newFile.renameTo(new File(absPath.concat("/").concat(md5))));
                }
                resources = new Resources();
                resources.setType("file");
                resources.setSize(pAlarmInfo.dwFacePicLen);
                resources.setName(md5);
                resources.setPath(filePath);
                resources.setMd5(md5);
                resources.setCreateTime(new Date());
                resourcesService.insert(resources);
            }catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}