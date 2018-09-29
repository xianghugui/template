package com.afdUtils;

import com.dev.jna.Native;

public class LoadUtils {
//    public static <T> T loadOSLibrary(String dirPath,String libname, Class<T> interfaceClass) {
//        System.out.println("路径：" + dirPath);
//        String filePath = dirPath+"/";
//        System.out.println(filePath);
//        if(Platform.isWindows()){
//            if(Platform.is64Bit()){
//                filePath += "win/x64/"+"lib"+libname+".dll";
//            }else{
//                filePath += "win/x86/"+"lib"+libname+".dll";
//            }
//        }else if(Platform.is64Bit() && Platform.isLinux()){
//            filePath = "/data/AFD_SDK/linux/x64/"+"lib"+libname+".so";
//        }else{
//             System.out.println("unsupported platform");
//             System.exit(0);
//        }
//
//        return loadLibrary(filePath,interfaceClass);
//    }
    
    public static <T> T loadLibrary(String filePath, Class<T> interfaceClass) {
        return Native.loadLibrary(filePath,interfaceClass);
    }
}
