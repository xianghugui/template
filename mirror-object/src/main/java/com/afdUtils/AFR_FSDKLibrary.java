package com.afdUtils;

import com.dev.jna.Library;
import com.dev.jna.NativeLong;
import com.dev.jna.Platform;
import com.dev.jna.Pointer;
import com.dev.jna.ptr.FloatByReference;
import com.dev.jna.ptr.PointerByReference;

public interface AFR_FSDKLibrary extends Library {
    AFR_FSDKLibrary INSTANCE = (AFR_FSDKLibrary)LoadUtils.loadLibrary(Platform.isWindows()?"libarcsoft_fsdk_face_recognition.dll":"libarcsoft_fsdk_face_recognition.so",AFR_FSDKLibrary.class);
    
    NativeLong AFR_FSDK_InitialEngine(
            String appid,
            String sdkid,
            Pointer pMem,
            int lMemSize,
            PointerByReference phEngine
    );

    NativeLong AFR_FSDK_ExtractFRFeature(
            Pointer hEngine,
            ASVLOFFSCREEN pImgData,
            AFR_FSDK_FACEINPUT pFaceRes,
            AFR_FSDK_FACEMODEL pFaceModels
    );

    NativeLong AFR_FSDK_FacePairMatching(
            Pointer hEngine,
            AFR_FSDK_FACEMODEL reffeature,
            AFR_FSDK_FACEMODEL probefeature,
            FloatByReference pfSimilScore
    );
    
    NativeLong AFR_FSDK_UninitialEngine(Pointer hEngine);

}