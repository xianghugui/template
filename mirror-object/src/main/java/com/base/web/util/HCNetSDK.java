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
import com.sun.jna.examples.win32.GDI32.RECT;
import com.sun.jna.examples.win32.W32API;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.ShortByReference;

import java.io.File;
import java.util.Arrays;
import java.util.List;

//SDK接口说明,HCNetSDK.dll
public interface HCNetSDK extends Library {

    String path = HCNetSDK.class.getResource("/").getPath().replaceAll("%20", " ")
            .replaceFirst("/", "").replace("/", File.separator) + "win" + File.separator + "hcnetsdk";

    HCNetSDK INSTANCE = (HCNetSDK) Native.loadLibrary(Platform.isWindows() ? path : "hcnetsdk",
            HCNetSDK.class);
    /***宏定义***/
    //常量

    public static final int MAX_NAMELEN = 16;    //DVR本地登陆名
    public static final int MAX_RIGHT = 32;    //设备支持的权限（1-12表示本地权限，13-32表示远程权限）
    public static final int NAME_LEN = 32;    //用户名长度
    public static final int PASSWD_LEN = 16;    //密码长度
    public static final int SERIALNO_LEN = 48;   //序列号长度
    public static final int MACADDR_LEN = 6;      //mac地址长度
    public static final int MAX_ETHERNET = 2;   //设备可配以太网络
    public static final int PATHNAME_LEN = 128;   //路径长度
    public static final int MAX_TIMESEGMENT_V30 = 8;    //9000设备最大时间段数
    public static final int MAX_TIMESEGMENT = 4;   //8000设备最大时间段数
    public static final int MAX_SHELTERNUM = 4;   //8000设备最大遮挡区域数
    public static final int MAX_DAYS = 7;      //每周天数
    public static final int PHONENUMBER_LEN = 32;   //pppoe拨号号码最大长度
    public static final int MAX_DISKNUM_V30 = 33;        //9000设备最大硬盘数/* 最多33个硬盘(包括16个内置SATA硬盘、1个eSATA硬盘和16个NFS盘) */
    public static final int MAX_DISKNUM = 16;     //8000设备最大硬盘数
    public static final int MAX_DISKNUM_V10 = 8;   //1.2版本之前版本
    public static final int MAX_WINDOW_V30 = 32; //9000设备本地显示最大播放窗口数
    public static final int MAX_WINDOW = 16;    //8000设备最大硬盘数
    public static final int MAX_VGA_V30 = 4;     //9000设备最大可接VGA数
    public static final int MAX_VGA = 1;    //8000设备最大可接VGA数
    public static final int MAX_USERNUM_V30 = 32;  //9000设备最大用户数
    public static final int MAX_USERNUM = 16;  //8000设备最大用户数
    public static final int MAX_EXCEPTIONNUM_V30 = 32;  //9000设备最大异常处理数
    public static final int MAX_EXCEPTIONNUM = 16;   //8000设备最大异常处理数
    public static final int MAX_LINK = 6;    //8000设备单通道最大视频流连接数
    public static final int MAX_DECPOOLNUM = 4;   //单路解码器每个解码通道最大可循环解码数
    public static final int MAX_DECNUM = 4;    //单路解码器的最大解码通道数（实际只有一个，其他三个保留）
    public static final int MAX_TRANSPARENTNUM = 2;   //单路解码器可配置最大透明通道数
    public static final int MAX_CYCLE_CHAN = 16;   //单路解码器最大轮循通道数
    public static final int MAX_DIRNAME_LENGTH = 80;   //最大目录长度
    public static final int MAX_STRINGNUM_V30 = 8;        //9000设备最大OSD字符行数数
    public static final int MAX_STRINGNUM = 4;   //8000设备最大OSD字符行数数
    public static final int MAX_STRINGNUM_EX = 8;   //8000定制扩展
    public static final int MAX_AUXOUT_V30 = 16;   //9000设备最大辅助输出数
    public static final int MAX_AUXOUT = 4;      //8000设备最大辅助输出数
    public static final int MAX_HD_GROUP = 16;   //9000设备最大硬盘组数
    public static final int MAX_NFS_DISK = 8;    //8000设备最大NFS硬盘数
    public static final int IW_ESSID_MAX_SIZE = 32;    //WIFI的SSID号长度
    public static final int IW_ENCODING_TOKEN_MAX = 32;   //WIFI密锁最大字节数
    public static final int MAX_SERIAL_NUM = 64;    //最多支持的透明通道路数
    public static final int MAX_DDNS_NUMS = 10;   //9000设备最大可配ddns数
    public static final int MAX_DOMAIN_NAME = 64;    /* 最大域名长度 */

    public static final int MAX_EMAIL_ADDR_LEN = 48;  //最大email地址长度
    public static final int MAX_EMAIL_PWD_LEN = 32;     //最大email密码长度
    public static final int MAXPROGRESS = 100;  //回放时的最大百分率
    public static final int MAX_SERIALNUM = 2;    //8000设备支持的串口数 1-232， 2-485
    public static final int CARDNUM_LEN = 20;    //卡号长度
    public static final int MAX_VIDEOOUT_V30 = 4;      //9000设备的视频输出数
    public static final int MAX_VIDEOOUT = 2;      //8000设备的视频输出数
    public static final int MAX_PRESET_V30 = 256;    /* 9000设备支持的云台预置点数 */
    public static final int MAX_TRACK_V30 = 256;    /* 9000设备支持的云台轨迹数 */
    public static final int MAX_CRUISE_V30 = 256;    /* 9000设备支持的云台巡航数 */
    public static final int MAX_PRESET = 128;    /* 8000设备支持的云台预置点数 */
    public static final int MAX_TRACK = 128;    /* 8000设备支持的云台轨迹数 */
    public static final int MAX_CRUISE = 128;    /* 8000设备支持的云台巡航数 */
    public static final int CRUISE_MAX_PRESET_NUMS = 32;    /* 一条巡航最多的巡航点 */
    public static final int MAX_SERIAL_PORT = 8;    //9000设备支持232串口数
    public static final int MAX_PREVIEW_MODE = 8;    /* 设备支持最大预览模式数目 1画面,4画面,9画面,16画面.... */
    public static final int MAX_MATRIXOUT = 16;  /* 最大模拟矩阵输出个数 */
    public static final int LOG_INFO_LEN = 11840; /* 日志附加信息 */
    public static final int DESC_LEN = 16;    /* 云台描述字符串长度 */
    public static final int PTZ_PROTOCOL_NUM = 200;   /* 9000最大支持的云台协议数 */
    public static final int MAX_AUDIO = 1;    //8000语音对讲通道数
    public static final int MAX_AUDIO_V30 = 2;   //9000语音对讲通道数
    public static final int MAX_CHANNUM = 16;   //8000设备最大通道数
    public static final int MAX_ALARMIN = 16;  //8000设备最大报警输入数
    public static final int MAX_ALARMOUT = 4;    //8000设备最大报警输出数
    //9000 IPC接入
    public static final int MAX_ANALOG_CHANNUM = 32;    //最大32个模拟通道
    public static final int MAX_ANALOG_ALARMOUT = 32;    //最大32路模拟报警输出
    public static final int MAX_ANALOG_ALARMIN = 32;    //最大32路模拟报警输入
    public static final int MAX_IP_DEVICE = 32;    //允许接入的最大IP设备数
    public static final int MAX_IP_CHANNEL = 32;   //允许加入的最多IP通道数
    public static final int MAX_IP_ALARMIN = 128;   //允许加入的最多报警输入数
    public static final int MAX_IP_ALARMOUT = 64; //允许加入的最多报警输出数

    /* 最大支持的通道数 最大模拟加上最大IP支持 */
    public static final int MAX_CHANNUM_V30 = (MAX_ANALOG_CHANNUM + MAX_IP_CHANNEL);//64
    public static final int MAX_ALARMOUT_V30 = (MAX_ANALOG_ALARMOUT + MAX_IP_ALARMOUT);//96
    public static final int MAX_ALARMIN_V30 = (MAX_ANALOG_ALARMIN + MAX_IP_ALARMIN);//160

    public static final int MAX_LICENSE_LEN = 16;
    public static final int VCA_MAX_POLYGON_POINT_NUM = 10;

    public static final int MAX_ID_NUM_LEN = 32;  //最大身份证号长度
    public static final int MAX_ID_NAME_LEN = 128;   //最大姓名长度
    public static final int MAX_ID_ADDR_LEN = 280;   //最大住址长度
    public static final int MAX_ID_ISSUING_AUTHORITY_LEN = 128; //最大签发机关长度
    /*******************全局错误码 begin**********************/
    public static final int NET_DVR_NOERROR = 0;    //没有错误
    public static final int NET_DVR_PASSWORD_ERROR = 1;    //用户名密码错误
    public static final int NET_DVR_NOENOUGHPRI = 2;//权限不足
    public static final int NET_DVR_NOINIT = 3;//没有初始化
    public static final int NET_DVR_CHANNEL_ERROR = 4;    //通道号错误
    public static final int NET_DVR_OVER_MAXLINK = 5;    //连接到DVR的客户端个数超过最大
    public static final int NET_DVR_VERSIONNOMATCH = 6;    //版本不匹配
    public static final int NET_DVR_NETWORK_FAIL_CONNECT = 7;//连接服务器失败
    public static final int NET_DVR_NETWORK_SEND_ERROR = 8;    //向服务器发送失败
    public static final int NET_DVR_NETWORK_RECV_ERROR = 9;    //从服务器接收数据失败
    public static final int NET_DVR_NETWORK_RECV_TIMEOUT = 10;    //从服务器接收数据超时
    public static final int NET_DVR_NETWORK_ERRORDATA = 11;    //传送的数据有误
    public static final int NET_DVR_ORDER_ERROR = 12;    //调用次序错误
    public static final int NET_DVR_OPERNOPERMIT = 13;    //无此权限
    public static final int NET_DVR_COMMANDTIMEOUT = 14;    //DVR命令执行超时
    public static final int NET_DVR_ERRORSERIALPORT = 15;    //串口号错误
    public static final int NET_DVR_ERRORALARMPORT = 16;    //报警端口错误
    public static final int NET_DVR_PARAMETER_ERROR = 17;//参数错误
    public static final int NET_DVR_CHAN_EXCEPTION = 18;    //服务器通道处于错误状态
    public static final int NET_DVR_NODISK = 19;    //没有硬盘
    public static final int NET_DVR_ERRORDISKNUM = 20;    //硬盘号错误
    public static final int NET_DVR_DISK_FULL = 21;    //服务器硬盘满
    public static final int NET_DVR_DISK_ERROR = 22;//服务器硬盘出错
    public static final int NET_DVR_NOSUPPORT = 23;//服务器不支持
    public static final int NET_DVR_BUSY = 24;//服务器忙
    public static final int NET_DVR_MODIFY_FAIL = 25;//服务器修改不成功
    public static final int NET_DVR_PASSWORD_FORMAT_ERROR = 26;//密码输入格式不正确
    public static final int NET_DVR_DISK_FORMATING = 27;    //硬盘正在格式化，不能启动操作
    public static final int NET_DVR_DVRNORESOURCE = 28;    //DVR资源不足
    public static final int NET_DVR_DVROPRATEFAILED = 29; //DVR操作失败
    public static final int NET_DVR_OPENHOSTSOUND_FAIL = 30; //打开PC声音失败
    public static final int NET_DVR_DVRVOICEOPENED = 31; //服务器语音对讲被占用
    public static final int NET_DVR_TIMEINPUTERROR = 32; //时间输入不正确
    public static final int NET_DVR_NOSPECFILE = 33;  //回放时服务器没有指定的文件
    public static final int NET_DVR_CREATEFILE_ERROR = 34;    //创建文件出错
    public static final int NET_DVR_FILEOPENFAIL = 35; //打开文件出错
    public static final int NET_DVR_OPERNOTFINISH = 36; //上次的操作还没有完成
    public static final int NET_DVR_GETPLAYTIMEFAIL = 37; //获取当前播放的时间出错
    public static final int NET_DVR_PLAYFAIL = 38; //播放出错
    public static final int NET_DVR_FILEFORMAT_ERROR = 39;//文件格式不正确
    public static final int NET_DVR_DIR_ERROR = 40;    //路径错误
    public static final int NET_DVR_ALLOC_RESOURCE_ERROR = 41;//资源分配错误
    public static final int NET_DVR_AUDIO_MODE_ERROR = 42;    //声卡模式错误
    public static final int NET_DVR_NOENOUGH_BUF = 43;    //缓冲区太小
    public static final int NET_DVR_CREATESOCKET_ERROR = 44;    //创建SOCKET出错
    public static final int NET_DVR_SETSOCKET_ERROR = 45;    //设置SOCKET出错
    public static final int NET_DVR_MAX_NUM = 46;    //个数达到最大
    public static final int NET_DVR_USERNOTEXIST = 47;    //用户不存在
    public static final int NET_DVR_WRITEFLASHERROR = 48;//写FLASH出错
    public static final int NET_DVR_UPGRADEFAIL = 49;//DVR升级失败
    public static final int NET_DVR_CARDHAVEINIT = 50; //解码卡已经初始化过
    public static final int NET_DVR_PLAYERFAILED = 51;    //调用播放库中某个函数失败
    public static final int NET_DVR_MAX_USERNUM = 52; //设备端用户数达到最大
    public static final int NET_DVR_GETLOCALIPANDMACFAIL = 53;//获得客户端的IP地址或物理地址失败
    public static final int NET_DVR_NOENCODEING = 54;    //该通道没有编码
    public static final int NET_DVR_IPMISMATCH = 55;    //IP地址不匹配
    public static final int NET_DVR_MACMISMATCH = 56;//MAC地址不匹配
    public static final int NET_DVR_UPGRADELANGMISMATCH = 57;//升级文件语言不匹配
    public static final int NET_DVR_MAX_PLAYERPORT = 58;//播放器路数达到最大
    public static final int NET_DVR_NOSPACEBACKUP = 59;//备份设备中没有足够空间进行备份
    public static final int NET_DVR_NODEVICEBACKUP = 60;    //没有找到指定的备份设备
    public static final int NET_DVR_PICTURE_BITS_ERROR = 61;    //图像素位数不符，限24色
    public static final int NET_DVR_PICTURE_DIMENSION_ERROR = 62;//图片高*宽超限， 限128*256
    public static final int NET_DVR_PICTURE_SIZ_ERROR = 63;    //图片大小超限，限100K
    public static final int NET_DVR_LOADPLAYERSDKFAILED = 64;    //载入当前目录下Player Sdk出错
    public static final int NET_DVR_LOADPLAYERSDKPROC_ERROR = 65;    //找不到Player Sdk中某个函数入口
    public static final int NET_DVR_LOADDSSDKFAILED = 66;    //载入当前目录下DSsdk出错
    public static final int NET_DVR_LOADDSSDKPROC_ERROR = 67;    //找不到DsSdk中某个函数入口
    public static final int NET_DVR_DSSDK_ERROR = 68;    //调用硬解码库DsSdk中某个函数失败
    public static final int NET_DVR_VOICEMONOPOLIZE = 69;    //声卡被独占
    public static final int NET_DVR_JOINMULTICASTFAILED = 70;    //加入多播组失败
    public static final int NET_DVR_CREATEDIR_ERROR = 71;    //建立日志文件目录失败
    public static final int NET_DVR_BINDSOCKET_ERROR = 72;    //绑定套接字失败
    public static final int NET_DVR_SOCKETCLOSE_ERROR = 73;    //socket连接中断，此错误通常是由于连接中断或目的地不可达
    public static final int NET_DVR_USERID_ISUSING = 74;    //注销时用户ID正在进行某操作
    public static final int NET_DVR_SOCKETLISTEN_ERROR = 75;    //监听失败
    public static final int NET_DVR_PROGRAM_EXCEPTION = 76;    //程序异常
    public static final int NET_DVR_WRITEFILE_FAILED = 77;    //写文件失败
    public static final int NET_DVR_FORMAT_READONLY = 78;//禁止格式化只读硬盘
    public static final int NET_DVR_WITHSAMEUSERNAME = 79;//用户配置结构中存在相同的用户名
    public static final int NET_DVR_DEVICETYPE_ERROR = 80; /*导入参数时设备型号不匹配*/
    public static final int NET_DVR_LANGUAGE_ERROR = 81; /*导入参数时语言不匹配*/
    public static final int NET_DVR_PARAVERSION_ERROR = 82; /*导入参数时软件版本不匹配*/
    public static final int NET_DVR_IPCHAN_NOTALIVE = 83; /*预览时外接IP通道不在线*/
    public static final int NET_DVR_RTSP_SDK_ERROR = 84;    /*加载高清IPC通讯库StreamTransClient.dll失败*/
    public static final int NET_DVR_CONVERT_SDK_ERROR = 85;    /*加载转码库失败*/
    public static final int NET_DVR_IPC_COUNT_OVERFLOW = 86; /*超出最大的ip接入通道数*/
    public static final int NET_PLAYM4_NOERROR = 500;    //no error
    public static final int NET_PLAYM4_PARA_OVER = 501;//input parameter is invalid;
    public static final int NET_PLAYM4_ORDER_ERROR = 502;//The order of the function to be called is error.
    public static final int NET_PLAYM4_TIMER_ERROR = 503;//Create multimedia clock failed;
    public static final int NET_PLAYM4_DEC_VIDEO_ERROR = 504;//Decode video data failed.
    public static final int NET_PLAYM4_DEC_AUDIO_ERROR = 505;//Decode audio data failed.
    public static final int NET_PLAYM4_ALLOC_MEMORY_ERROR = 506;    //Allocate memory failed.
    public static final int NET_PLAYM4_OPEN_FILE_ERROR = 507;    //Open the file failed.
    public static final int NET_PLAYM4_CREATE_OBJ_ERROR = 508;//Create thread or event failed
    public static final int NET_PLAYM4_CREATE_DDRAW_ERROR = 509;//Create DirectDraw object failed.
    public static final int NET_PLAYM4_CREATE_OFFSCREEN_ERROR = 510;//failed when creating off-screen surface.
    public static final int NET_PLAYM4_BUF_OVER = 511;    //buffer is overflow
    public static final int NET_PLAYM4_CREATE_SOUND_ERROR = 512;    //failed when creating audio device.
    public static final int NET_PLAYM4_SET_VOLUME_ERROR = 513;//Set volume failed
    public static final int NET_PLAYM4_SUPPORT_FILE_ONLY = 514;//The function only support play file.
    public static final int NET_PLAYM4_SUPPORT_STREAM_ONLY = 515;//The function only support play stream.
    public static final int NET_PLAYM4_SYS_NOT_SUPPORT = 516;//System not support.
    public static final int NET_PLAYM4_FILEHEADER_UNKNOWN = 517;    //No file header.
    public static final int NET_PLAYM4_VERSION_INCORRECT = 518;    //The version of decoder and encoder is not adapted.
    public static final int NET_PALYM4_INIT_DECODER_ERROR = 519;    //Initialize decoder failed.
    public static final int NET_PLAYM4_CHECK_FILE_ERROR = 520;    //The file data is unknown.
    public static final int NET_PLAYM4_INIT_TIMER_ERROR = 521;    //Initialize multimedia clock failed.
    public static final int NET_PLAYM4_BLT_ERROR = 522;//Blt failed.
    public static final int NET_PLAYM4_UPDATE_ERROR = 523;//Update failed.
    public static final int NET_PLAYM4_OPEN_FILE_ERROR_MULTI = 524; //openfile error, streamtype is multi
    public static final int NET_PLAYM4_OPEN_FILE_ERROR_VIDEO = 525; //openfile error, streamtype is video
    public static final int NET_PLAYM4_JPEG_COMPRESS_ERROR = 526; //JPEG compress error
    public static final int NET_PLAYM4_EXTRACT_NOT_SUPPORT = 527;    //Don't support the version of this file.
    public static final int NET_PLAYM4_EXTRACT_DATA_ERROR = 528;    //extract video data failed.
    /*******************全局错误码 end**********************/
    /*************************************************
     NET_DVR_IsSupport()返回值
     1－9位分别表示以下信息（位与是TRUE)表示支持；
     **************************************************/
    public static final int NET_DVR_SUPPORT_DDRAW = 0x01;//支持DIRECTDRAW，如果不支持，则播放器不能工作；
    public static final int NET_DVR_SUPPORT_BLT = 0x02;//显卡支持BLT操作，如果不支持，则播放器不能工作；
    public static final int NET_DVR_SUPPORT_BLTFOURCC = 0x04;//显卡BLT支持颜色转换，如果不支持，播放器会用软件方法作RGB转换；
    public static final int NET_DVR_SUPPORT_BLTSHRINKX = 0x08;//显卡BLT支持X轴缩小；如果不支持，系统会用软件方法转换；
    public static final int NET_DVR_SUPPORT_BLTSHRINKY = 0x10;//显卡BLT支持Y轴缩小；如果不支持，系统会用软件方法转换；
    public static final int NET_DVR_SUPPORT_BLTSTRETCHX = 0x20;//显卡BLT支持X轴放大；如果不支持，系统会用软件方法转换；
    public static final int NET_DVR_SUPPORT_BLTSTRETCHY = 0x40;//显卡BLT支持Y轴放大；如果不支持，系统会用软件方法转换；
    public static final int NET_DVR_SUPPORT_SSE = 0x80;//CPU支持SSE指令，Intel Pentium3以上支持SSE指令；
    public static final int NET_DVR_SUPPORT_MMX = 0x100;//CPU支持MMX指令集，Intel Pentium3以上支持SSE指令；
    /**********************云台控制命令 begin*************************/
    public static final int LIGHT_PWRON = 2;    /* 接通灯光电源 */
    public static final int WIPER_PWRON = 3;    /* 接通雨刷开关 */
    public static final int FAN_PWRON = 4;    /* 接通风扇开关 */
    public static final int HEATER_PWRON = 5;    /* 接通加热器开关 */
    public static final int AUX_PWRON1 = 6;    /* 接通辅助设备开关 */
    public static final int AUX_PWRON2 = 7;    /* 接通辅助设备开关 */
    public static final int SET_PRESET = 8;    /* 设置预置点 */
    public static final int CLE_PRESET = 9;    /* 清除预置点 */
    public static final int ZOOM_IN = 11;    /* 焦距以速度SS变大(倍率变大) */
    public static final int ZOOM_OUT = 12;    /* 焦距以速度SS变小(倍率变小) */
    public static final int FOCUS_NEAR = 13; /* 焦点以速度SS前调 */
    public static final int FOCUS_FAR = 14; /* 焦点以速度SS后调 */
    public static final int IRIS_OPEN = 15; /* 光圈以速度SS扩大 */
    public static final int IRIS_CLOSE = 16; /* 光圈以速度SS缩小 */
    public static final int TILT_UP = 21;    /* 云台以SS的速度上仰 */
    public static final int TILT_DOWN = 22;    /* 云台以SS的速度下俯 */
    public static final int PAN_LEFT = 23;    /* 云台以SS的速度左转 */
    public static final int PAN_RIGHT = 24;    /* 云台以SS的速度右转 */
    public static final int UP_LEFT = 25;    /* 云台以SS的速度上仰和左转 */
    public static final int UP_RIGHT = 26;    /* 云台以SS的速度上仰和右转 */
    public static final int DOWN_LEFT = 27;    /* 云台以SS的速度下俯和左转 */
    public static final int DOWN_RIGHT = 28;    /* 云台以SS的速度下俯和右转 */
    public static final int PAN_AUTO = 29;    /* 云台以SS的速度左右自动扫描 */
    public static final int FILL_PRE_SEQ = 30;    /* 将预置点加入巡航序列 */
    public static final int SET_SEQ_DWELL = 31;    /* 设置巡航点停顿时间 */
    public static final int SET_SEQ_SPEED = 32;    /* 设置巡航速度 */
    public static final int CLE_PRE_SEQ = 33;/* 将预置点从巡航序列中删除 */
    public static final int STA_MEM_CRUISE = 34;/* 开始记录轨迹 */
    public static final int STO_MEM_CRUISE = 35;/* 停止记录轨迹 */
    public static final int RUN_CRUISE = 36;    /* 开始轨迹 */
    public static final int RUN_SEQ = 37;    /* 开始巡航 */
    public static final int STOP_SEQ = 38;    /* 停止巡航 */
    public static final int GOTO_PRESET = 39;    /* 快球转到预置点 */

    /**********************云台控制命令 end*************************/
    /*************************************************
     回放时播放控制命令宏定义
     NET_DVR_PlayBackControl
     NET_DVR_PlayControlLocDisplay
     NET_DVR_DecPlayBackCtrl的宏定义
     具体支持查看函数说明和代码
     **************************************************/
    public static final int NET_DVR_PLAYSTART = 1;//开始播放
    public static final int NET_DVR_PLAYSTOP = 2;//停止播放
    public static final int NET_DVR_PLAYPAUSE = 3;//暂停播放
    public static final int NET_DVR_PLAYRESTART = 4;//恢复播放
    public static final int NET_DVR_PLAYFAST = 5;//快放
    public static final int NET_DVR_PLAYSLOW = 6;//慢放
    public static final int NET_DVR_PLAYNORMAL = 7;//正常速度
    public static final int NET_DVR_PLAYFRAME = 8;//单帧放
    public static final int NET_DVR_PLAYSTARTAUDIO = 9;//打开声音
    public static final int NET_DVR_PLAYSTOPAUDIO = 10;//关闭声音
    public static final int NET_DVR_PLAYAUDIOVOLUME = 11;//调节音量
    public static final int NET_DVR_PLAYSETPOS = 12;//改变文件回放的进度
    public static final int NET_DVR_PLAYGETPOS = 13;//获取文件回放的进度
    public static final int NET_DVR_PLAYGETTIME = 14;//获取当前已经播放的时间(按文件回放的时候有效)
    public static final int NET_DVR_PLAYGETFRAME = 15;//获取当前已经播放的帧数(按文件回放的时候有效)
    public static final int NET_DVR_GETTOTALFRAMES = 16;//获取当前播放文件总的帧数(按文件回放的时候有效)
    public static final int NET_DVR_GETTOTALTIME = 17;//获取当前播放文件总的时间(按文件回放的时候有效)
    public static final int NET_DVR_THROWBFRAME = 20;//丢B帧
    public static final int NET_DVR_SETSPEED = 24;//设置码流速度
    public static final int NET_DVR_KEEPALIVE = 25;//保持与设备的心跳(如果回调阻塞，建议2秒发送一次)
    //远程按键定义如下：
    /* key value send to CONFIG program */
    public static final int KEY_CODE_1 = 1;
    public static final int KEY_CODE_2 = 2;
    public static final int KEY_CODE_3 = 3;
    public static final int KEY_CODE_4 = 4;
    public static final int KEY_CODE_5 = 5;
    public static final int KEY_CODE_6 = 6;
    public static final int KEY_CODE_7 = 7;
    public static final int KEY_CODE_8 = 8;
    public static final int KEY_CODE_9 = 9;
    public static final int KEY_CODE_0 = 10;
    public static final int KEY_CODE_POWER = 11;
    public static final int KEY_CODE_MENU = 12;
    public static final int KEY_CODE_ENTER = 13;
    public static final int KEY_CODE_CANCEL = 14;
    public static final int KEY_CODE_UP = 15;
    public static final int KEY_CODE_DOWN = 16;
    public static final int KEY_CODE_LEFT = 17;
    public static final int KEY_CODE_RIGHT = 18;
    public static final int KEY_CODE_EDIT = 19;
    public static final int KEY_CODE_ADD = 20;
    public static final int KEY_CODE_MINUS = 21;
    public static final int KEY_CODE_PLAY = 22;
    public static final int KEY_CODE_REC = 23;
    public static final int KEY_CODE_PAN = 24;
    public static final int KEY_CODE_M = 25;
    public static final int KEY_CODE_A = 26;
    public static final int KEY_CODE_F1 = 27;
    public static final int KEY_CODE_F2 = 28;

    /* for PTZ control */
    public static final int KEY_PTZ_UP_START = KEY_CODE_UP;
    public static final int KEY_PTZ_UP_STO = 32;
    public static final int KEY_PTZ_DOWN_START = KEY_CODE_DOWN;
    public static final int KEY_PTZ_DOWN_STOP = 33;
    public static final int KEY_PTZ_LEFT_START = KEY_CODE_LEFT;
    public static final int KEY_PTZ_LEFT_STOP = 34;
    public static final int KEY_PTZ_RIGHT_START = KEY_CODE_RIGHT;
    public static final int KEY_PTZ_RIGHT_STOP = 35;
    public static final int KEY_PTZ_AP1_START = KEY_CODE_EDIT;/* 光圈+ */
    public static final int KEY_PTZ_AP1_STOP = 36;
    public static final int KEY_PTZ_AP2_START = KEY_CODE_PAN;/* 光圈- */
    public static final int KEY_PTZ_AP2_STOP = 37;
    public static final int KEY_PTZ_FOCUS1_START = KEY_CODE_A;/* 聚焦+ */
    public static final int KEY_PTZ_FOCUS1_STOP = 38;
    public static final int KEY_PTZ_FOCUS2_START = KEY_CODE_M;/* 聚焦- */
    public static final int KEY_PTZ_FOCUS2_STOP = 39;
    public static final int KEY_PTZ_B1_START = 40;/* 变倍+ */
    public static final int KEY_PTZ_B1_STOP = 41;
    public static final int KEY_PTZ_B2_START = 42;/* 变倍- */
    public static final int KEY_PTZ_B2_STOP = 43;
    //9000新增
    public static final int KEY_CODE_11 = 44;
    public static final int KEY_CODE_12 = 45;
    public static final int KEY_CODE_13 = 46;
    public static final int KEY_CODE_14 = 47;
    public static final int KEY_CODE_15 = 48;
    public static final int KEY_CODE_16 = 49;
    /*************************参数配置命令 begin*******************************/
//用于NET_DVR_SetDVRConfig和NET_DVR_GetDVRConfig,注意其对应的配置结构
    public static final int NET_DVR_GET_DEVICECFG = 100;    //获取设备参数
    public static final int NET_DVR_SET_DEVICECFG = 101;    //设置设备参数
    public static final int NET_DVR_GET_NETCFG = 102;    //获取网络参数
    public static final int NET_DVR_SET_NETCFG = 103;    //设置网络参数
    public static final int NET_DVR_GET_PICCFG = 104;    //获取图象参数
    public static final int NET_DVR_SET_PICCFG = 105;    //设置图象参数
    public static final int NET_DVR_GET_COMPRESSCFG = 106;    //获取压缩参数
    public static final int NET_DVR_SET_COMPRESSCFG = 107;    //设置压缩参数
    public static final int NET_DVR_GET_RECORDCFG = 108;    //获取录像时间参数
    public static final int NET_DVR_SET_RECORDCFG = 109;    //设置录像时间参数
    public static final int NET_DVR_GET_DECODERCFG = 110;    //获取解码器参数
    public static final int NET_DVR_SET_DECODERCFG = 111;    //设置解码器参数
    public static final int NET_DVR_GET_RS232CFG = 112;    //获取232串口参数
    public static final int NET_DVR_SET_RS232CFG = 113;    //设置232串口参数
    public static final int NET_DVR_GET_ALARMINCFG = 114;    //获取报警输入参数
    public static final int NET_DVR_SET_ALARMINCFG = 115;    //设置报警输入参数
    public static final int NET_DVR_GET_ALARMOUTCFG = 116;    //获取报警输出参数
    public static final int NET_DVR_SET_ALARMOUTCFG = 117;    //设置报警输出参数
    public static final int NET_DVR_GET_TIMECFG = 118;    //获取DVR时间
    public static final int NET_DVR_SET_TIMECFG = 119;        //设置DVR时间
    public static final int NET_DVR_GET_PREVIEWCFG = 120;    //获取预览参数
    public static final int NET_DVR_SET_PREVIEWCFG = 121;    //设置预览参数
    public static final int NET_DVR_GET_VIDEOOUTCFG = 122;    //获取视频输出参数
    public static final int NET_DVR_SET_VIDEOOUTCFG = 123;    //设置视频输出参数
    public static final int NET_DVR_GET_USERCFG = 124;    //获取用户参数
    public static final int NET_DVR_SET_USERCFG = 125;    //设置用户参数
    public static final int NET_DVR_GET_EXCEPTIONCFG = 126;    //获取异常参数
    public static final int NET_DVR_SET_EXCEPTIONCFG = 127;    //设置异常参数
    public static final int NET_DVR_GET_ZONEANDDST = 128;    //获取时区和夏时制参数
    public static final int NET_DVR_SET_ZONEANDDST = 129;    //设置时区和夏时制参数
    public static final int NET_DVR_GET_SHOWSTRING = 130;    //获取叠加字符参数
    public static final int NET_DVR_SET_SHOWSTRING = 131;    //设置叠加字符参数
    public static final int NET_DVR_GET_EVENTCOMPCFG = 132;    //获取事件触发录像参数
    public static final int NET_DVR_SET_EVENTCOMPCFG = 133;    //设置事件触发录像参数
    public static final int NET_DVR_GET_AUXOUTCFG = 140;    //获取报警触发辅助输出设置(HS设备辅助输出2006-02-28)
    public static final int NET_DVR_SET_AUXOUTCFG = 141;    //设置报警触发辅助输出设置(HS设备辅助输出2006-02-28)
    public static final int NET_DVR_GET_PREVIEWCFG_AUX = 142;    //获取-s系列双输出预览参数(-s系列双输出2006-04-13)
    public static final int NET_DVR_SET_PREVIEWCFG_AUX = 143;    //设置-s系列双输出预览参数(-s系列双输出2006-04-13)
    public static final int NET_DVR_GET_PICCFG_EX = 200;    //获取图象参数(SDK_V14扩展命令)
    public static final int NET_DVR_SET_PICCFG_EX = 201;    //设置图象参数(SDK_V14扩展命令)
    public static final int NET_DVR_GET_USERCFG_EX = 202;    //获取用户参数(SDK_V15扩展命令)
    public static final int NET_DVR_SET_USERCFG_EX = 203;    //设置用户参数(SDK_V15扩展命令)
    public static final int NET_DVR_GET_COMPRESSCFG_EX = 204;    //获取压缩参数(SDK_V15扩展命令2006-05-15)
    public static final int NET_DVR_SET_COMPRESSCFG_EX = 205;    //设置压缩参数(SDK_V15扩展命令2006-05-15)
    public static final int NET_DVR_GET_NETAPPCFG = 222;    //获取网络应用参数 NTP/DDNS/EMAIL
    public static final int NET_DVR_SET_NETAPPCFG = 223;    //设置网络应用参数 NTP/DDNS/EMAIL
    public static final int NET_DVR_GET_NTPCFG = 224;    //获取网络应用参数 NTP
    public static final int NET_DVR_SET_NTPCFG = 225;    //设置网络应用参数 NTP
    public static final int NET_DVR_GET_DDNSCFG = 226;    //获取网络应用参数 DDNS
    public static final int NET_DVR_SET_DDNSCFG = 227;        //设置网络应用参数 DDNS
    //对应NET_DVR_EMAILPARA
    public static final int NET_DVR_GET_EMAILCFG = 228;    //获取网络应用参数 EMAIL
    public static final int NET_DVR_SET_EMAILCFG = 229;    //设置网络应用参数 EMAIL
    public static final int NET_DVR_GET_NFSCFG = 230;    /* NFS disk config */
    public static final int NET_DVR_SET_NFSCFG = 231;    /* NFS disk config */
    public static final int NET_DVR_GET_SHOWSTRING_EX = 238;    //获取叠加字符参数扩展(支持8条字符)
    public static final int NET_DVR_SET_SHOWSTRING_EX = 239;    //设置叠加字符参数扩展(支持8条字符)
    public static final int NET_DVR_GET_NETCFG_OTHER = 244;    //获取网络参数
    public static final int NET_DVR_SET_NETCFG_OTHER = 245;    //设置网络参数
    //对应NET_DVR_EMAILCFG结构
    public static final int NET_DVR_GET_EMAILPARACFG = 250;    //Get EMAIL parameters
    public static final int NET_DVR_SET_EMAILPARACFG = 251;    //Setup EMAIL parameters
    public static final int NET_DVR_GET_DDNSCFG_EX = 274;//获取扩展DDNS参数
    public static final int NET_DVR_SET_DDNSCFG_EX = 275;//设置扩展DDNS参数
    public static final int NET_DVR_SET_PTZPOS = 292;    //云台设置PTZ位置
    public static final int NET_DVR_GET_PTZPOS = 293;        //云台获取PTZ位置
    public static final int NET_DVR_GET_PTZSCOPE = 294;//云台获取PTZ范围
    /***************************DS9000新增命令(_V30) begin *****************************/
//网络(NET_DVR_NETCFG_V30结构)
    public static final int NET_DVR_GET_NETCFG_V30 = 1000;    //获取网络参数
    public static final int NET_DVR_SET_NETCFG_V30 = 1001;    //设置网络参数
    //图象(NET_DVR_PICCFG_V30结构)
    public static final int NET_DVR_GET_PICCFG_V30 = 1002;    //获取图象参数
    public static final int NET_DVR_SET_PICCFG_V30 = 1003;    //设置图象参数
    //录像时间(NET_DVR_RECORD_V30结构)
    public static final int NET_DVR_GET_RECORDCFG_V30 = 1004;    //获取录像参数
    public static final int NET_DVR_SET_RECORDCFG_V30 = 1005;    //设置录像参数
    //用户(NET_DVR_USER_V30结构)
    public static final int NET_DVR_GET_USERCFG_V30 = 1006;    //获取用户参数
    public static final int NET_DVR_SET_USERCFG_V30 = 1007;    //设置用户参数
    //9000DDNS参数配置(NET_DVR_DDNSPARA_V30结构)
    public static final int NET_DVR_GET_DDNSCFG_V30 = 1010;    //获取DDNS(9000扩展)
    public static final int NET_DVR_SET_DDNSCFG_V30 = 1011;    //设置DDNS(9000扩展)
    //EMAIL功能(NET_DVR_EMAILCFG_V30结构)
    public static final int NET_DVR_GET_EMAILCFG_V30 = 1012;//获取EMAIL参数
    public static final int NET_DVR_SET_EMAILCFG_V30 = 1013;//设置EMAIL参数
    //巡航参数 (NET_DVR_CRUISE_PARA结构)
    public static final int NET_DVR_GET_CRUISE = 1020;
    public static final int NET_DVR_SET_CRUISE = 1021;
    //报警输入结构参数 (NET_DVR_ALARMINCFG_V30结构)
    public static final int NET_DVR_GET_ALARMINCFG_V30 = 1024;
    public static final int NET_DVR_SET_ALARMINCFG_V30 = 1025;
    //报警输出结构参数 (NET_DVR_ALARMOUTCFG_V30结构)
    public static final int NET_DVR_GET_ALARMOUTCFG_V30 = 1026;
    public static final int NET_DVR_SET_ALARMOUTCFG_V30 = 1027;
    //视频输出结构参数 (NET_DVR_VIDEOOUT_V30结构)
    public static final int NET_DVR_GET_VIDEOOUTCFG_V30 = 1028;
    public static final int NET_DVR_SET_VIDEOOUTCFG_V30 = 1029;
    //叠加字符结构参数 (NET_DVR_SHOWSTRING_V30结构)
    public static final int NET_DVR_GET_SHOWSTRING_V30 = 1030;
    public static final int NET_DVR_SET_SHOWSTRING_V30 = 1031;
    //异常结构参数 (NET_DVR_EXCEPTION_V30结构)
    public static final int NET_DVR_GET_EXCEPTIONCFG_V30 = 1034;
    public static final int NET_DVR_SET_EXCEPTIONCFG_V30 = 1035;
    //串口232结构参数 (NET_DVR_RS232CFG_V30结构)
    public static final int NET_DVR_GET_RS232CFG_V30 = 1036;
    public static final int NET_DVR_SET_RS232CFG_V30 = 1037;
    //压缩参数 (NET_DVR_COMPRESSIONCFG_V30结构)
    public static final int NET_DVR_GET_COMPRESSCFG_V30 = 1040;
    public static final int NET_DVR_SET_COMPRESSCFG_V30 = 1041;
    //获取485解码器参数 (NET_DVR_DECODERCFG_V30结构)
    public static final int NET_DVR_GET_DECODERCFG_V30 = 1042;    //获取解码器参数
    public static final int NET_DVR_SET_DECODERCFG_V30 = 1043;    //设置解码器参数
    //获取预览参数 (NET_DVR_PREVIEWCFG_V30结构)
    public static final int NET_DVR_GET_PREVIEWCFG_V30 = 1044;    //获取预览参数
    public static final int NET_DVR_SET_PREVIEWCFG_V30 = 1045;    //设置预览参数
    //辅助预览参数 (NET_DVR_PREVIEWCFG_AUX_V30结构)
    public static final int NET_DVR_GET_PREVIEWCFG_AUX_V30 = 1046;    //获取辅助预览参数
    public static final int NET_DVR_SET_PREVIEWCFG_AUX_V30 = 1047;    //设置辅助预览参数
    //IP接入配置参数 （NET_DVR_IPPARACFG结构）
    public static final int NET_DVR_GET_IPPARACFG = 1048;    //获取IP接入配置信息
    public static final int NET_DVR_SET_IPPARACFG = 1049;    //设置IP接入配置信息
    //IP报警输入接入配置参数 （NET_DVR_IPALARMINCFG结构）
    public static final int NET_DVR_GET_IPALARMINCFG = 1050;    //获取IP报警输入接入配置信息
    public static final int NET_DVR_SET_IPALARMINCFG = 1051;   //设置IP报警输入接入配置信息
    //IP报警输出接入配置参数 （NET_DVR_IPALARMOUTCFG结构）
    public static final int NET_DVR_GET_IPALARMOUTCFG = 1052;   //获取IP报警输出接入配置信息
    public static final int NET_DVR_SET_IPALARMOUTCFG = 1053;  //设置IP报警输出接入配置信息
    //硬盘管理的参数获取 (NET_DVR_HDCFG结构)
    public static final int NET_DVR_GET_HDCFG = 1054;    //获取硬盘管理配置参数
    public static final int NET_DVR_SET_HDCFG = 1055;    //设置硬盘管理配置参数
    //盘组管理的参数获取 (NET_DVR_HDGROUP_CFG结构)
    public static final int NET_DVR_GET_HDGROUP_CFG = 1056;    //获取盘组管理配置参数
    public static final int NET_DVR_SET_HDGROUP_CFG = 1057;    //设置盘组管理配置参数
    //设备编码类型配置(NET_DVR_COMPRESSION_AUDIO结构)
    public static final int NET_DVR_GET_COMPRESSCFG_AUD = 1058;     //获取设备语音对讲编码参数
    public static final int NET_DVR_SET_COMPRESSCFG_AUD = 1059;     //设置设备语音对讲编码参数
    /***************************DS9000新增命令(_V30) end *****************************/
    /*************************参数配置命令 end*******************************/
    /*******************查找文件和日志函数返回值*************************/
    public static final int NET_DVR_FILE_SUCCESS = 1000;    //获得文件信息
    public static final int NET_DVR_FILE_NOFIND = 1001;    //没有文件
    public static final int NET_DVR_ISFINDING = 1002;//正在查找文件
    public static final int NET_DVR_NOMOREFILE = 1003;//查找文件时没有更多的文件
    public static final int NET_DVR_FILE_EXCEPTION = 1004;//查找文件时异常
    /*********************回调函数类型 begin************************/
    public static final int COMM_ALARM = 0x1100;    //8000报警信息主动上传
    public static final int COMM_TRADEINFO = 0x1500;  //ATMDVR主动上传交易信息
    public static final int COMM_ALARM_V30 = 0x4000;//9000报警信息主动上传
    public static final int COMM_ALARM_RULE = 0x1102;//行为分析信息上传
    public static final int COMM_ALARM_PDC = 0x1103;//客流量统计报警上传
    public static final int COMM_UPLOAD_PLATE_RESULT = 0x2800;//交通抓拍结果上传
    public static final int COMM_ITS_PLATE_RESULT = 0x3050;//交通抓拍的终端图片上传
    public static final int COMM_IPCCFG = 0x4001;//9000设备IPC接入配置改变报警信息主动上传
    public static final int COMM_ITS_PARK_VEHICLE = 0x3056;//停车场数据上传
    public static final int COMM_ALARM_TFS = 0x1113; //交通取证报警信息
    public static final int COMM_ALARM_TPS_V41 = 0x1114; //交通事件报警信息扩展
    public static final int COMM_ALARM_AID_V41 = 0x1115; //交通事件报警信息扩展
    public static final int COMM_UPLOAD_FACESNAP_RESULT = 0x1112;  //人脸识别结果上传
    public static final int COMM_SNAP_MATCH_ALARM = 0x2902;  //黑名单比对结果上传
    public static final int COMM_ALARM_ACS = 0x5002; //门禁主机报警信息
    public static final int COMM_ID_INFO_ALARM = 0x5200; //门禁身份证刷卡信息
    public static final int COMM_VCA_ALARM = 0x4993; //智能检测通用报警
    /*************操作异常类型(消息方式, 回调方式(保留))****************/
    public static final int EXCEPTION_EXCHANGE = 0x8000;//用户交互时异常
    public static final int EXCEPTION_AUDIOEXCHANGE = 0x8001;//语音对讲异常
    public static final int EXCEPTION_ALARM = 0x8002;//报警异常
    public static final int EXCEPTION_PREVIEW = 0x8003;//网络预览异常
    public static final int EXCEPTION_SERIAL = 0x8004;//透明通道异常
    public static final int EXCEPTION_RECONNECT = 0x8005;    //预览时重连
    public static final int EXCEPTION_ALARMRECONNECT = 0x8006;//报警时重连
    public static final int EXCEPTION_SERIALRECONNECT = 0x8007;//透明通道重连
    public static final int EXCEPTION_PLAYBACK = 0x8010;//回放异常
    public static final int EXCEPTION_DISKFMT = 0x8011;//硬盘格式化
    /********************预览回调函数*********************/
    public static final int NET_DVR_SYSHEAD = 1;//系统头数据
    public static final int NET_DVR_STREAMDATA = 2;//视频流数据（包括复合流和音视频分开的视频流数据）
    public static final int NET_DVR_AUDIOSTREAMDATA = 3;//音频流数据
    public static final int NET_DVR_STD_VIDEODATA = 4;//标准视频流数据
    public static final int NET_DVR_STD_AUDIODATA = 5;//标准音频流数据
    //回调预览中的状态和消息
    public static final int NET_DVR_REALPLAYEXCEPTION = 111;//预览异常
    public static final int NET_DVR_REALPLAYNETCLOSE = 112;//预览时连接断开
    public static final int NET_DVR_REALPLAY5SNODATA = 113;//预览5s没有收到数据
    public static final int NET_DVR_REALPLAYRECONNECT = 114;//预览重连
    /********************回放回调函数*********************/
    public static final int NET_DVR_PLAYBACKOVER = 101;//回放数据播放完毕
    public static final int NET_DVR_PLAYBACKEXCEPTION = 102;//回放异常
    public static final int NET_DVR_PLAYBACKNETCLOSE = 103;//回放时候连接断开
    public static final int NET_DVR_PLAYBACK5SNODATA = 104;    //回放5s没有收到数据
    /*********************回调函数类型 end************************/
//设备型号(DVR类型)
    /* 设备类型 */
    public static final int DVR = 1;            /*对尚未定义的dvr类型返回NETRET_DVR*/
    public static final int ATMDVR = 2;        /*atm dvr*/
    public static final int DVS = 3;            /*DVS*/
    public static final int DEC = 4;            /* 6001D */
    public static final int ENC_DEC = 5;            /* 6001F */
    public static final int DVR_HC = 6;            /*8000HC*/
    public static final int DVR_HT = 7;            /*8000HT*/
    public static final int DVR_HF = 8;            /*8000HF*/
    public static final int DVR_HS = 9;            /* 8000HS DVR(no audio) */
    public static final int DVR_HTS = 10;         /* 8016HTS DVR(no audio) */
    public static final int DVR_HB = 11;         /* HB DVR(SATA HD) */
    public static final int DVR_HCS = 12;         /* 8000HCS DVR */
    public static final int DVS_A = 13;         /* 带ATA硬盘的DVS */
    public static final int DVR_HC_S = 14;         /* 8000HC-S */
    public static final int DVR_HT_S = 15;         /* 8000HT-S */
    public static final int DVR_HF_S = 16;         /* 8000HF-S */
    public static final int DVR_HS_S = 17;         /* 8000HS-S */
    public static final int ATMDVR_S = 18;         /* ATM-S */
    public static final int LOWCOST_DVR = 19;            /*7000H系列*/
    public static final int DEC_MAT = 20;         /*多路解码器*/
    public static final int DVR_MOBILE = 21;            /* mobile DVR */
    public static final int DVR_HD_S = 22;        /* 8000HD-S */
    public static final int DVR_HD_SL = 23;            /* 8000HD-SL */
    public static final int DVR_HC_SL = 24;            /* 8000HC-SL */
    public static final int DVR_HS_ST = 25;            /* 8000HS_ST */
    public static final int DVS_HW = 26;         /* 6000HW */
    public static final int IPCAM = 30;            /*IP 摄像机*/
    public static final int MEGA_IPCAM = 31;            /*X52MF系列,752MF,852MF*/
    public static final int IPCAM_X62MF = 32;            /*X62MF系列可接入9000设备,762MF,862MF*/
    public static final int IPDOME = 40;            /*IP标清快球*/
    public static final int MEGA_IPDOME = 41;     /*IP高清快球*/
    public static final int IPMOD = 50;            /*IP 模块*/
    public static final int DS71XX_H = 71;            /* DS71XXH_S */
    public static final int DS72XX_H_S = 72;            /* DS72XXH_S */
    public static final int DS73XX_H_S = 73;            /* DS73XXH_S */
    public static final int DS81XX_HS_S = 81;            /* DS81XX_HS_S */
    public static final int DS81XX_HL_S = 82;            /* DS81XX_HL_S */
    public static final int DS81XX_HC_S = 83;            /* DS81XX_HC_S */
    public static final int DS81XX_HD_S = 84;            /* DS81XX_HD_S */
    public static final int DS81XX_HE_S = 85;            /* DS81XX_HE_S */
    public static final int DS81XX_HF_S = 86;            /* DS81XX_HF_S */
    public static final int DS81XX_AH_S = 87;            /* DS81XX_AH_S */
    public static final int DS81XX_AHF_S = 88;            /* DS81XX_AHF_S */
    public static final int DS90XX_HF_S = 90;       /*DS90XX_HF_S*/
    public static final int DS91XX_HF_S = 91;             /*DS91XX_HF_S*/
    public static final int DS91XX_HD_S = 92;            /*91XXHD-S(MD)*/

    /* 操作 */
//主类型
    public static final int MAJOR_OPERATION = 0x3;
    //次类型
    public static final int MINOR_START_DVR = 0x41; /* 开机 */
    public static final int MINOR_STOP_DVR = 0x42;/* 关机 */
    public static final int MINOR_STOP_ABNORMAL = 0x43;/* 异常关机 */
    public static final int MINOR_REBOOT_DVR = 0x44;   /*本地重启设备*/
    public static final int MINOR_LOCAL_LOGIN = 0x50; /* 本地登陆 */
    public static final int MINOR_LOCAL_LOGOUT = 0x51; /* 本地注销登陆 */
    public static final int MINOR_LOCAL_CFG_PARM = 0x52; /* 本地配置参数 */
    public static final int MINOR_LOCAL_PLAYBYFILE = 0x53; /* 本地按文件回放或下载 */
    public static final int MINOR_LOCAL_PLAYBYTIME = 0x54; /* 本地按时间回放或下载*/
    public static final int MINOR_LOCAL_START_REC = 0x55; /* 本地开始录像 */
    public static final int MINOR_LOCAL_STOP_REC = 0x56; /* 本地停止录像 */
    public static final int MINOR_LOCAL_PTZCTRL = 0x57; /* 本地云台控制 */
    public static final int MINOR_LOCAL_PREVIEW = 0x58;/* 本地预览 (保留不使用)*/
    public static final int MINOR_LOCAL_MODIFY_TIME = 0x59;/* 本地修改时间(保留不使用) */
    public static final int MINOR_LOCAL_UPGRADE = 0x5a;/* 本地升级 */
    public static final int MINOR_LOCAL_RECFILE_OUTPUT = 0x5b;   /* 本地备份录象文件 */
    public static final int MINOR_LOCAL_FORMAT_HDD = 0x5c;  /* 本地初始化硬盘 */
    public static final int MINOR_LOCAL_CFGFILE_OUTPUT = 0x5d;  /* 导出本地配置文件 */
    public static final int MINOR_LOCAL_CFGFILE_INPUT = 0x5e;  /* 导入本地配置文件 */
    public static final int MINOR_LOCAL_COPYFILE = 0x5f;  /* 本地备份文件 */
    public static final int MINOR_LOCAL_LOCKFILE = 0x60;  /* 本地锁定录像文件 */
    public static final int MINOR_LOCAL_UNLOCKFILE = 0x61;   /* 本地解锁录像文件 */
    public static final int MINOR_LOCAL_DVR_ALARM = 0x62;  /* 本地手动清除和触发报警*/
    public static final int MINOR_IPC_ADD = 0x63;  /* 本地添加IPC */
    public static final int MINOR_IPC_DEL = 0x64;  /* 本地删除IPC */
    public static final int MINOR_IPC_SET = 0x65;  /* 本地设置IPC */
    public static final int MINOR_LOCAL_START_BACKUP = 0x66;    /* 本地开始备份 */
    public static final int MINOR_LOCAL_STOP_BACKUP = 0x67;/* 本地停止备份*/
    public static final int MINOR_LOCAL_COPYFILE_START_TIME = 0x68;/* 本地备份开始时间*/
    public static final int MINOR_LOCAL_COPYFILE_END_TIME = 0x69;    /* 本地备份结束时间*/
    public static final int MINOR_REMOTE_LOGIN = 0x70;/* 远程登录 */
    public static final int MINOR_REMOTE_LOGOUT = 0x71;/* 远程注销登陆 */
    public static final int MINOR_REMOTE_START_REC = 0x72;/* 远程开始录像 */
    public static final int MINOR_REMOTE_STOP_REC = 0x73;/* 远程停止录像 */
    public static final int MINOR_START_TRANS_CHAN = 0x74;/* 开始透明传输 */
    public static final int MINOR_STOP_TRANS_CHAN = 0x75; /* 停止透明传输 */
    public static final int MINOR_REMOTE_GET_PARM = 0x76;/* 远程获取参数 */
    public static final int MINOR_REMOTE_CFG_PARM = 0x77;/* 远程配置参数 */
    public static final int MINOR_REMOTE_GET_STATUS = 0x78;/* 远程获取状态 */
    public static final int MINOR_REMOTE_ARM = 0x79; /* 远程布防 */
    public static final int MINOR_REMOTE_DISARM = 0x7a;/* 远程撤防 */
    public static final int MINOR_REMOTE_REBOOT = 0x7b; /* 远程重启 */
    public static final int MINOR_START_VT = 0x7c;/* 开始语音对讲 */
    public static final int MINOR_STOP_VT = 0x7d;/* 停止语音对讲 */
    public static final int MINOR_REMOTE_UPGRADE = 0x7e; /* 远程升级 */
    public static final int MINOR_REMOTE_PLAYBYFILE = 0x7f; /* 远程按文件回放 */
    public static final int MINOR_REMOTE_PLAYBYTIME = 0x80; /* 远程按时间回放 */
    public static final int MINOR_REMOTE_PTZCTRL = 0x81; /* 远程云台控制 */
    public static final int MINOR_REMOTE_FORMAT_HDD = 0x82;  /* 远程格式化硬盘 */
    public static final int MINOR_REMOTE_STOP = 0x83;  /* 远程关机 */
    public static final int MINOR_REMOTE_LOCKFILE = 0x84;/* 远程锁定文件 */
    public static final int MINOR_REMOTE_UNLOCKFILE = 0x85;/* 远程解锁文件 */
    public static final int MINOR_REMOTE_CFGFILE_OUTPUT = 0x86;   /* 远程导出配置文件 */
    public static final int MINOR_REMOTE_CFGFILE_INTPUT = 0x87;   /* 远程导入配置文件 */
    public static final int MINOR_REMOTE_RECFILE_OUTPUT = 0x88;   /* 远程导出录象文件 */
    public static final int MINOR_REMOTE_DVR_ALARM = 0x89;    /* 远程手动清除和触发报警*/
    public static final int MINOR_REMOTE_IPC_ADD = 0x8a;  /* 远程添加IPC */
    public static final int MINOR_REMOTE_IPC_DEL = 0x8b;/* 远程删除IPC */
    public static final int MINOR_REMOTE_IPC_SET = 0x8c; /* 远程设置IPC */
    public static final int MINOR_REBOOT_VCA_LIB = 0x8d;        /*重启智能库*/

    /*日志附加信息*/
//主类型
    public static final int MAJOR_INFORMATION = 0x4;   /*附加信息*/
    //次类型
    public static final int MINOR_HDD_INFO = 0xa1;/*硬盘信息*/
    public static final int MINOR_SMART_INFO = 0xa2;   /*SMART信息*/
    public static final int MINOR_REC_START = 0xa3;   /*开始录像*/
    public static final int MINOR_REC_STOP = 0xa4;/*停止录像*/
    public static final int MINOR_REC_OVERDUE = 0xa5;/*过期录像删除*/
    public static final int MINOR_LINK_START = 0xa6; // ivms，多路解码器等连接前端设备
    public static final int MINOR_LINK_STOP = 0xa7;// ivms，多路解码器等断开前端设备　
    //当日志的主类型为MAJOR_OPERATION=03，次类型为MINOR_LOCAL_CFG_PARM=0x52或者MINOR_REMOTE_GET_PARM=0x76或者MINOR_REMOTE_CFG_PARM=0x77时，dwParaType:参数类型有效，其含义如下：
    public static final int PARA_VIDEOOUT = 0x1;
    public static final int PARA_IMAGE = 0x2;
    public static final int PARA_ENCODE = 0x4;
    public static final int PARA_NETWORK = 0x8;
    public static final int PARA_ALARM = 0x10;
    public static final int PARA_EXCEPTION = 0x20;
    public static final int PARA_DECODER = 0x40; /*解码器*/
    public static final int PARA_RS232 = 0x80;
    public static final int PARA_PREVIEW = 0x100;
    public static final int PARA_SECURITY = 0x200;
    public static final int PARA_DATETIME = 0x400;
    public static final int PARA_FRAMETYPE = 0x800;  /*帧格式*/
    public static final int PARA_VCA_RULE = 0x1000;    //行为规则
    //SDK_V222
//智能设备类型
    public static final int DS6001_HF_B = 60;//行为分析：DS6001-HF/B
    public static final int DS6001_HF_P = 61;//车牌识别：DS6001-HF/P
    public static final int DS6002_HF_B = 62;//双机跟踪：DS6002-HF/B
    public static final int DS6101_HF_B = 63;//行为分析：DS6101-HF/B
    public static final int IVMS_2000 = 64;//智能分析仪
    public static final int DS9000_IVS = 65;//9000系列智能DVR
    public static final int DS8004_AHL_A = 66;//智能ATM, DS8004AHL-S/A
    public static final int DS6101_HF_P = 67;//车牌识别：DS6101-HF/P
    //能力获取命令
    public static final int VCA_DEV_ABILITY = 0x100;//设备智能分析的总能力
    public static final int VCA_CHAN_ABILITY = 0x110;//行为分析能力
    //获取/设置大接口参数配置命令
//车牌识别（NET_VCA_PLATE_CFG）;
    public static final int NET_DVR_SET_PLATECFG = 150;//设置车牌识别参数

    public static final int NET_DVR_GET_PLATECFG = 151;    //获取车牌识别参数
    //行为对应（NET_VCA_RULECFG）
    public static final int NET_DVR_SET_RULECFG = 152;    //设置行为分析规则
    public static final int NET_DVR_GET_RULECFG = 153;//获取行为分析规则,
    //双摄像机标定参数（NET_DVR_LF_CFG）
    public static final int NET_DVR_SET_LF_CFG = 160;//设置双摄像机的配置参数
    public static final int NET_DVR_GET_LF_CFG = 161;//获取双摄像机的配置参数
    //智能分析仪取流配置结构
    public static final int NET_DVR_SET_IVMS_STREAMCFG = 162;    //设置智能分析仪取流参数
    public static final int NET_DVR_GET_IVMS_STREAMCFG = 163;    //获取智能分析仪取流参数
    //智能控制参数结构
    public static final int NET_DVR_SET_VCA_CTRLCFG = 164; //设置智能控制参数
    public static final int NET_DVR_GET_VCA_CTRLCFG = 165;     //获取智能控制参数
    //屏蔽区域NET_VCA_MASK_REGION_LIST
    public static final int NET_DVR_SET_VCA_MASK_REGION = 166;     //设置屏蔽区域参数
    public static final int NET_DVR_GET_VCA_MASK_REGION = 167;     //获取屏蔽区域参数
    //ATM进入区域 NET_VCA_ENTER_REGION
    public static final int NET_DVR_SET_VCA_ENTER_REGION = 168; //设置进入区域参数
    public static final int NET_DVR_GET_VCA_ENTER_REGION = 169;     //获取进入区域参数
    //标定线配置NET_VCA_LINE_SEGMENT_LIST
    public static final int NET_DVR_SET_VCA_LINE_SEGMENT = 170;     //设置标定线
    public static final int NET_DVR_GET_VCA_LINE_SEGMENT = 171;     //获取标定线
    // ivms屏蔽区域NET_IVMS_MASK_REGION_LIST
    public static final int NET_DVR_SET_IVMS_MASK_REGION = 172;     //设置IVMS屏蔽区域参数
    public static final int NET_DVR_GET_IVMS_MASK_REGION = 173;     //获取IVMS屏蔽区域参数
    // ivms进入检测区域NET_IVMS_ENTER_REGION
    public static final int NET_DVR_SET_IVMS_ENTER_REGION = 174; //设置IVMS进入区域参数
    public static final int NET_DVR_GET_IVMS_ENTER_REGION = 175; //获取IVMS进入区域参数
    public static final int NET_DVR_SET_IVMS_BEHAVIORCFG = 176;//设置智能分析仪行为规则参数
    public static final int NET_DVR_GET_IVMS_BEHAVIORCFG = 177;    //获取智能分析仪行为规则参数
    public static final int NET_DVR_GET_TRAVERSE_PLANE_DETECTION = 3360; //获取越界侦测配置

    public static final int NET_DVR_GET_CARD_CFG = 2116; //获取卡参数
    public static final int NET_DVR_SET_CARD_CFG = 2117; //设置卡参数
    /**********************设备类型 end***********************/

    /*************************************************
     参数配置结构、参数(其中_V30为9000新增)
     **************************************************/
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
