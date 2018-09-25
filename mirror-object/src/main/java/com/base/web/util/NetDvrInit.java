package com.base.web.util;

import com.base.web.service.resource.FileService;
import com.sun.jna.NativeLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
public class NetDvrInit implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private FmsgCallBack FmsgCallBack;

    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        hCNetSDK.NET_DVR_Init();
        hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(FmsgCallBack, null);

        HCNetSDK.NET_DVR_DEVICEINFO_V30 struDeviceInfoV30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        NativeLong id = hCNetSDK.NET_DVR_Login_V30("192.168.2.254", (short) 8000, "admin", "cdx123456", struDeviceInfoV30);
        hCNetSDK.NET_DVR_SetupAlarmChan_V30(id);
    }

    public static void main(String[] arg) {
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


