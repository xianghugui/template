package com.base.web.util;

import com.base.web.bean.Camera;
import com.base.web.bean.ServerDevice;
import com.base.web.service.CameraService;
import com.base.web.service.ServerDeviceService;
import com.sun.jna.NativeLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NetDvrInit implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private FmsgCallBack fmsgCallBack;

    @Autowired
    private CameraService cameraService;

    @Autowired
    private ServerDeviceService serverDeviceService;

    static HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;

    static HCNetSDK.NET_DVR_DEVICEINFO_V30 struDeviceInfoV30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 项目启动时设置所有已分配摄像头的人脸抓拍报警回调函数
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //初始化SDK
        if (hCNetSDK.NET_DVR_Init()) {
            //设置报警回调函数
            hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(fmsgCallBack, null);
            List<Camera> cameras = cameraService.createQuery().where(Camera.Property.STATUS, 1).list();
            for (Camera camera : cameras) {
                //登陆
                Long userId = login(camera);
                if (userId == 0xFFFFFFFF || userId == 0xFFFFFFFFL) {
                    logger.error("启动项目时，IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                            + "password:"+ camera.getPassword() + "登陆失败，错误码：" + getLastError());
                    continue;
                } else {
                    camera.setUserId(userId);
                }
                //报警布防
                Long alarmHandleId = setupAlarmChan(userId);
                if (alarmHandleId == 0xFFFFFFFF || alarmHandleId == 0xFFFFFFFFL) {
                    //布防失败，解除关联
                    camera.setStatus(0);
                    cameraService.update(camera);
                    serverDeviceService.createDelete().where(ServerDevice.Property.deviceId, camera.getId()).exec();
                    logger.error("启动项目时，IP:" + camera.getIp() + "port:" + camera.getPort() + "account:" + camera.getAccount()
                            + "password:"+ camera.getPassword() + "报警布防失败，错误码：" + getLastError());
                    continue;
                } else {
                    camera.setAlarmHandleId(alarmHandleId);
                }
                for (int i = 0; i < 10; i++) {
                    FaceFeatureUtil.ENGINEMAPS.put(camera.getId() + i,new FaceFeatureUtil());
                }
                System.out.println("摄像头登陆以及报警布防完成。。。。。。。。。。。。。。。。。。。。。。。。。。。");
                cameraService.update(camera);
            }
            FaceFeatureUtil.ENGINEMAPS.put(0L, new FaceFeatureUtil());
        }

    }

    /**
     * 设备登陆
     * @param camera
     * @return
     */
    public static Long login(Camera camera){
        return hCNetSDK.NET_DVR_Login_V30(camera.getIp(), camera.getPort(),
                camera.getAccount(), camera.getPassword(), struDeviceInfoV30).longValue();
    }

    /**
     * 设备登出
     * @param userId
     * @return
     */
    public static Boolean logout(Long userId){
        return hCNetSDK.NET_DVR_Logout(new NativeLong(userId));
    }

    /**
     * 设置报警回调函数
     * @param userId
     * @return
     */
    public static Long setupAlarmChan(Long userId) {
        return hCNetSDK.NET_DVR_SetupAlarmChan_V30(new NativeLong(userId)).longValue();
    }

    /**
     * 报警撤防
     * @param alarmHandleId
     * @return
     */
    public static Boolean closeAlarmChan(Long alarmHandleId) {
        return hCNetSDK.NET_DVR_CloseAlarmChan_V30(new NativeLong(alarmHandleId));
    }

    /**
     * 返回最后操作的错误码
     * @return
     */
    public static int getLastError() {
        return hCNetSDK.NET_DVR_GetLastError();
    }

}


