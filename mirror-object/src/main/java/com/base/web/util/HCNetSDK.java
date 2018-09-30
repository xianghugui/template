/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * HCNetSDK.java
 *
 * Created on 2009-9-14, 19:31:34
 */

/**
 * @author Xubinfeng
 */

package com.base.web.util;

import com.sun.jna.*;

import java.util.Arrays;
import java.util.List;

//SDK接口说明,HCNetSDK.dll
public interface HCNetSDK extends Library {

    //linux下的etc/ld.so.conf添加/data/apache-tomcat-8.5.31/webapps/ROOT/WEB-INF/classes/linux-x86-64/HCNetSDKCom/,然后执行ldconfig
    HCNetSDK INSTANCE = Native.loadLibrary("hcnetsdk", HCNetSDK.class);
    /***宏定义***/
    //常量

    public static final int MAX_NAMELEN = 16;    //DVR本地登陆名
    public static final int MAX_RIGHT = 32;    //设备支持的权限（1-12表示本地权限，13-32表示远程权限）
    public static final int NAME_LEN = 32;    //用户名长度
    public static final int PASSWD_LEN = 16;    //密码长度
    public static final int SERIALNO_LEN = 48;   //序列号长度
    public static final int MACADDR_LEN = 6;      //mac地址长度


//报警设备信息
    public static class NET_DVR_ALARMER extends Structure {
        public byte byUserIDValid;                 /* userid是否有效 0-无效，1-有效 */
        public byte bySerialValid;                 /* 序列号是否有效 0-无效，1-有效 */
        public byte byVersionValid;                /* 版本号是否有效 0-无效，1-有效 */
        public byte byDeviceNameValid;             /* 设备名字是否有效 0-无效，1-有效 */
        public byte byMacAddrValid;                /* MAC地址是否有效 0-无效，1-有效 */
        public byte byLinkPortValid;               /* login端口是否有效 0-无效，1-有效 */
        public byte byDeviceIPValid;               /* 设备IP是否有效 0-无效，1-有效 */
        public byte bySocketIPValid;               /* socket ip是否有效 0-无效，1-有效 */
        public NativeLong lUserID;                       /* NET_DVR_Login()返回值, 布防时有效 */
        public byte[] sSerialNumber = new byte[SERIALNO_LEN];    /* 序列号 */
        public int dwDeviceVersion;                /* 版本信息 高16位表示主版本，低16位表示次版本*/
        public byte[] sDeviceName = new byte[NAME_LEN];            /* 设备名字 */
        public byte[] byMacAddr = new byte[MACADDR_LEN];        /* MAC地址 */
        public short wLinkPort;                     /* link port */
        public byte[] sDeviceIP = new byte[128];                /* IP地址 */
        public byte[] sSocketIP = new byte[128];                /* 报警主动上传时的socket IP地址 */
        public byte byIpProtocol;                  /* Ip协议 0-IPV4, 1-IPV6 */
        public byte[] byRes2 = new byte[11];

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"byUserIDValid", "bySerialValid", "byVersionValid", "byDeviceNameValid", "byMacAddrValid"
                    , "byLinkPortValid", "byDeviceIPValid", "bySocketIPValid", "lUserID", "sSerialNumber", "dwDeviceVersion", "sDeviceName"
                    , "byMacAddr", "wLinkPort", "sDeviceIP", "sSocketIP", "byIpProtocol", "byRes2"});
        }
    }

    public static class NET_DVR_DEVICEINFO_V30 extends Structure {
        public byte[] sSerialNumber = new byte[SERIALNO_LEN];  //序列号
        public byte byAlarmInPortNum;                //报警输入个数
        public byte byAlarmOutPortNum;                //报警输出个数
        public byte byDiskNum;                    //硬盘个数
        public byte byDVRType;                    //设备类型, 1:DVR 2:ATM DVR 3:DVS ......
        public byte byChanNum;                    //模拟通道个数
        public byte byStartChan;                    //起始通道号,例如DVS-1,DVR - 1
        public byte byAudioChanNum;                //语音通道数
        public byte byIPChanNum;                    //最大数字通道个数
        public byte[] byRes1 = new byte[24];                    //保留

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"sSerialNumber", "byAlarmInPortNum", "byAlarmOutPortNum", "byDiskNum", "byDVRType",
                    "byChanNum", "byStartChan", "byAudioChanNum", "byIPChanNum", "byRes1"});
        }
    }

    //人脸抓拍结果
    public static class NET_VCA_FACESNAP_RESULT extends Structure {
        public int dwSize;             // 结构大小
        public int dwRelativeTime;     // 相对时标
        public int dwAbsTime;            // 绝对时标
        public int dwFacePicID;       //人脸图ID
        public int dwFaceScore;        //人脸评分,0-100
        public NET_VCA_TARGET_INFO struTargetInfo;//报警目标信息
        public NET_VCA_RECT struRect;      //人脸子图区域
        public NET_VCA_DEV_INFO struDevInfo;    //前端设备信息
        public int dwFacePicLen;        //人脸子图的长度，为0表示没有图片，大于0表示有图片
        public int dwBackgroundPicLen; //背景图的长度，为0表示没有图片，大于0表示有图片(保留)
        public byte bySmart;            //IDS设备返回0(默认值)，Smart Functiom Return 1
        public byte byAlarmEndMark;//报警结束标记0-保留，1-结束标记（该字段结合人脸ID字段使用，表示该ID对应的下报警结束，主要提供给NVR使用，用于判断报警结束，提取识别图片数据中，清晰度最高的图片）
        public byte byRepeatTimes;   //重复报警次数，0-无意义
        public byte byUploadEventDataType;//人脸图片数据长传方式：0-二进制数据，1-URL
        public NET_VCA_HUMAN_FEATURE struFeature;  //人体属性
        public float fStayDuration;  //停留画面中时间(单位: 秒)
        public byte[] sStorageIP = new byte[16];        //存储服务IP地址
        public short wStoragePort;            //存储服务端口号
        public short wDevInfoIvmsChannelEx;     //与NET_VCA_DEV_INFO里的byIvmsChannel含义相同，能表示更大的值。老客户端用byIvmsChannel能继续兼容，但是最大到255。新客户端版本请使用wDevInfoIvmsChannelEx。
        public byte[] byRes1 = new byte[16];              // 保留字节
        public Pointer pBuffer1;  //人脸子图的图片数据
        public Pointer pBuffer2;  //背景图的图片数据（保留，通过查找背景图接口可以获取背景图）
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwRelativeTime", "dwAbsTime", "dwFacePicID", "dwFaceScore",
                    "struTargetInfo", "struRect", "struDevInfo", "dwFacePicLen", "dwBackgroundPicLen","bySmart","byAlarmEndMark",
                    "byRepeatTimes", "byUploadEventDataType", "struFeature", "fStayDuration", "sStorageIP","wStoragePort","wDevInfoIvmsChannelEx",
                    "byRes1", "pBuffer1", "pBuffer2"
            });
        }
    }

    //报警目标信息
    public static class NET_VCA_TARGET_INFO extends Structure {
        public int dwID;
        public NET_VCA_RECT struRect;
        public byte[] byRes = new byte[4];

        @Override
        protected List<String> getFieldOrder() {
            return  Arrays.asList(new String[]{"dwID","struRect","byRes"});
        }
    }

    //区域框参数
    public static class NET_VCA_RECT extends Structure {
        public float fX;
        public float fY;
        public float fWidth;
        public float fHeight;
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"fX","fY","fWidth","fHeight"});
        }
    }

    //前端设备信息
    public static class NET_VCA_DEV_INFO extends Structure {
        public NET_DVR_IPADDR struDevIP;
        public short wPort;
        public byte byChannel;
        public byte byIvmsChannel;
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"struDevIP","wPort","byChannel","byIvmsChannel"});
        }
    }

    //人体特征识别结果结构体
    public static class NET_VCA_HUMAN_FEATURE extends Structure {
        public byte byAgeGroup;    //年龄段,参见 HUMAN_AGE_GROUP_ENUM
        public byte bySex;         //性别, 1 - 男 , 2 - 女
        public byte byEyeGlass;    //是否戴眼镜 1 - 不戴, 2 - 戴
        public byte[] byRes = new byte[13];    //保留
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"byAgeGroup","bySex","byEyeGlass","byRes"});
        }
    }

    public static class NET_DVR_IPADDR extends Structure {
        public byte[] sIpV4 = new byte[16];
        public byte[] byRes = new byte[128];
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"sIpV4","byRes"});
        }

        @Override
        public String toString() {
            return "NET_DVR_IPADDR.sIpV4: " + new String(sIpV4) + "\n" + "NET_DVR_IPADDR.byRes: " + new String(byRes) + "\n";
        }
    }


    /***API函数声明,详细说明见API手册***/

    public static interface FMSGCallBack extends Callback {
        public void invoke(NativeLong lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser);
    }

    boolean NET_DVR_Init();

    boolean NET_DVR_SetDVRMessageCallBack_V30(FMSGCallBack fMessageCallBack, Pointer pUser);

    NativeLong NET_DVR_Login_V30(String sDVRIP, short wDVRPort, String sUserName, String sPassword, NET_DVR_DEVICEINFO_V30 lpDeviceInfo);

    boolean NET_DVR_Logout(NativeLong lUserID);

    int NET_DVR_GetLastError();

    NativeLong NET_DVR_SetupAlarmChan_V30(NativeLong lUserID);

    boolean NET_DVR_CloseAlarmChan_V30(NativeLong lAlarmHandle);


}
