package com.base.web.util;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class NetDvrInit {

    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    public static void main(String[] args) {
        hCNetSDK.NET_DVR_Init();
        hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(new FmsgCallBack(), null);

        HCNetSDK.NET_DVR_DEVICEINFO_V30 struDeviceInfoV30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        NativeLong id = hCNetSDK.NET_DVR_Login_V30("192.168.2.254", (short) 8000, "admin", "cdx123456", struDeviceInfoV30);
        hCNetSDK.NET_DVR_SetupAlarmChan_V30(id);
        int i = 0;
        while (true) {
            i++;
        }
    }

}

/**
 * 报警回调函数
 */
class FmsgCallBack implements HCNetSDK.FMSGCallBack {
    @Override
    public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.RECV_ALARM pAlarmInfo, int dwBufLen, Pointer pUser) throws IOException {
        System.out.println("进来了");

//        File file = new File("C:\\Users\\Geek\\Desktop\\1.jpg");
//        FileImageOutputStream imageOutput = new FileImageOutputStream(file);
//        imageOutput.write(pAlarmInfo.pBuffer1);//将byte写入硬盘
//        try {
//            imageOutput.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}

