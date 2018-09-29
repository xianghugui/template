package com.afdUtils;

import com.dev.jna.Library;
import com.dev.jna.NativeLong;
import com.dev.jna.Platform;
import com.dev.jna.Pointer;
import com.dev.jna.ptr.PointerByReference;

public interface AFD_FSDKLibrary extends Library {
    
    AFD_FSDKLibrary INSTANCE = (AFD_FSDKLibrary)LoadUtils.loadLibrary(Platform.isWindows()?"libarcsoft_fsdk_face_detection.dll":"libarcsoft_fsdk_face_detection.so",AFD_FSDKLibrary.class);
    
    NativeLong AFD_FSDK_InitialFaceEngine(String appid, String sdkid, Pointer pMem, int lMemSize, PointerByReference phEngine, int iOrientPriority, int nScale, int nMaxFaceNum);

    NativeLong AFD_FSDK_StillImageFaceDetection(Pointer hEngine, ASVLOFFSCREEN pImgData, PointerByReference pFaceRes);

    NativeLong AFD_FSDK_UninitialFaceEngine(Pointer hEngine);

}