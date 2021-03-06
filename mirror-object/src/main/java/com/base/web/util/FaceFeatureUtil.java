package com.base.web.util;

import com.afdUtils.*;
import com.afdUtils.utils.BufferInfo;
import com.afdUtils.utils.ImageLoader;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.PointerByReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FaceFeatureUtil {

    //存放人脸识别引擎，根据摄像头ID获取对应引擎，key：0为目标检索等使用
    public static final Map<Long, FaceFeatureUtil> ENGINEMAPS = new HashMap<>();


    /**
     * 虹软人脸识别
     */
    private static final String APPID = NetDvrInit.isWin ? "GaNub6zqMbUnpmfBkM9BNJzvCHF8atej4mSjD5a5rzbg" : "GaNub6zqMbUnpmfBkM9BNJzo2syzZUHfef2nPzQpnQKh";
    private static final String FD_SDKKEY = NetDvrInit.isWin ? "6RuX1NkZyFCsjVDsynZaCpKFBXWdeeTXoGJsCYERaYWi" : "5n7iBGp5wqVyJqSwq1WjGmvdAqcJ1bpEBBVWH83LZw2M";
    private static final String FR_SDKKEY = NetDvrInit.isWin ? "6RuX1NkZyFCsjVDsynZaCpKjq8ZLi8XGyK5AdKijT6og" : "5n7iBGp5wqVyJqSwq1WjGmw7pSezPmb6DDvUhStyjLiC";

    private static final int FD_WORKBUF_SIZE = 20 * 1024 * 1024;
    private static final int FR_WORKBUF_SIZE = 40 * 1024 * 1024;
    private static final int MAX_FACE_NUM = 50;
    private static final boolean bUseBGRToEngine = true;
    private Pointer hFREngine;
    private Pointer hFDEngine;
    private Pointer pFDWorkMem;
    private Pointer pFRWorkMem;

    public FaceFeatureUtil() {
        // init Engine
        pFDWorkMem = CLibrary.INSTANCE.malloc(FD_WORKBUF_SIZE);
        pFRWorkMem = CLibrary.INSTANCE.malloc(FR_WORKBUF_SIZE);

        PointerByReference phFDEngine = new PointerByReference();
        NativeLong ret = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_InitialFaceEngine(APPID, FD_SDKKEY, pFDWorkMem, FD_WORKBUF_SIZE, phFDEngine, _AFD_FSDK_OrientPriority.AFD_FSDK_OPF_0_HIGHER_EXT, 32, MAX_FACE_NUM);
        if (ret.longValue() != 0) {
            CLibrary.INSTANCE.free(pFDWorkMem);
            CLibrary.INSTANCE.free(pFRWorkMem);
            System.out.println(String.format("AFD_FSDK_InitialFaceEngine ret 0x%x", ret.longValue()));
            throw new RuntimeException();
        }

        // print FDEngine version
        hFDEngine = phFDEngine.getValue();

        PointerByReference phFREngine = new PointerByReference();
        ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_InitialEngine(APPID, FR_SDKKEY, pFRWorkMem, FR_WORKBUF_SIZE, phFREngine);
        if (ret.longValue() != 0) {
            clearFaceEngine();
            System.out.println(String.format("AFR_FSDK_InitialEngine ret 0x%x", ret.longValue()));
            throw new RuntimeException();
        }
        hFREngine = phFREngine.getValue();
    }

    /**
     * 清除人脸检索引擎和人脸比对引擎
     */
    public void clearFaceEngine() {
        AFD_FSDKLibrary.INSTANCE.AFD_FSDK_UninitialFaceEngine(hFDEngine);
        AFR_FSDKLibrary.INSTANCE.AFR_FSDK_UninitialEngine(hFREngine);
        CLibrary.INSTANCE.free(pFDWorkMem);
        CLibrary.INSTANCE.free(pFRWorkMem);
    }

    /**
     * 返回人脸特征值
     *
     * @param file
     * @return
     * @throws Exception
     */
    public byte[][] returnFaceFeature(File file) {
        return extractFace(file);
    }

    /**
     * 传入图片返回byte[]人脸特征值
     *
     * @param file
     * @return AFR_FSDK_FACEMODEL
     */
    public  byte[][] extractFace(File file) {
        // load Image Data
        ASVLOFFSCREEN inputImg = loadImage(file);
        FaceInfo[] faceInfos = doFaceDetection(hFDEngine, inputImg);
        if (faceInfos.length < 1) {
            return null;
        }
        //获取图片里面能检测到的所有人脸
        AFR_FSDK_FACEINPUT[] faceinput = new AFR_FSDK_FACEINPUT[faceInfos.length];
        for (int i = 0; i < faceInfos.length; i++) {
            faceinput[i] = new AFR_FSDK_FACEINPUT();
            faceinput[i].lOrient = faceInfos[i].orient;
            faceinput[i].rcFace.left = faceInfos[i].left;
            faceinput[i].rcFace.top = faceInfos[i].top;
            faceinput[i].rcFace.right = faceInfos[i].right;
            faceinput[i].rcFace.bottom = faceInfos[i].bottom;
        }

        //获取图片里面能检测到的所有人脸特征值
        AFR_FSDK_FACEMODEL faceFeature = new AFR_FSDK_FACEMODEL();
        byte[][] bytes = new byte[faceinput.length][0];
        for (int i = 0; i < faceinput.length; i++) {
            NativeLong nativeLong = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_ExtractFRFeature(hFREngine, inputImg, faceinput[i], faceFeature);
            if (nativeLong.longValue() != 0) {
                bytes[i] = new byte[]{};
                continue;
            }
            try {
                bytes[i] = faceFeature.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    /**
     * 人脸检测数组
     * 检测图片中存在的人脸
     *
     * @param hFDEngine hFD引擎
     * @param inputImg  待检测人人脸土拍你
     * @return
     */
    public FaceInfo[] doFaceDetection(Pointer hFDEngine, ASVLOFFSCREEN inputImg) {
        FaceInfo[] faceInfo = new FaceInfo[0];
        PointerByReference ppFaceRes = new PointerByReference();
        NativeLong ret = AFD_FSDKLibrary.INSTANCE.AFD_FSDK_StillImageFaceDetection(hFDEngine, inputImg, ppFaceRes);
        if (ret.longValue() != 0) {
            System.out.println(String.format("AFD_FSDK_StillImageFaceDetection ret 0x%x", ret.longValue()));
            return faceInfo;
        }
        AFD_FSDK_FACERES faceRes = new AFD_FSDK_FACERES(ppFaceRes.getValue());
        if (faceRes.nFace > 0) {
            faceInfo = new FaceInfo[faceRes.nFace];
            for (int i = 0; i < faceRes.nFace; i++) {
                MRECT rect = new MRECT(new Pointer(Pointer.nativeValue(faceRes.rcFace.getPointer()) + faceRes.rcFace.size() * i));
                int orient = faceRes.lfaceOrient.getPointer().getInt(i * 4);
                faceInfo[i] = new FaceInfo();
                faceInfo[i].left = rect.left;
                faceInfo[i].top = rect.top;
                faceInfo[i].right = rect.right;
                faceInfo[i].bottom = rect.bottom;
                faceInfo[i].orient = orient;
            }
        }
        return faceInfo;
    }

    //提供特征值获取分数
    public float compareFaceSimilarity(byte[] faceFeatureA, byte[] faceFeatureB) throws Exception {
        AFR_FSDK_FACEMODEL faceA = AFR_FSDK_FACEMODEL.fromByteArray(faceFeatureA);
        AFR_FSDK_FACEMODEL faceB = AFR_FSDK_FACEMODEL.fromByteArray(faceFeatureB);
        FloatByReference fSimilScore = new FloatByReference(0.0f);
        NativeLong ret = AFR_FSDKLibrary.INSTANCE.AFR_FSDK_FacePairMatching(hFREngine, faceA, faceB, fSimilScore);
        faceA.freeUnmanaged();
        faceB.freeUnmanaged();
        if (ret.longValue() != 0) {
            System.out.println(String.format("AFR_FSDK_FacePairMatching failed:ret 0x%x" ,ret.longValue()));
            return 0.0f;
        }
        return fSimilScore.getValue();
    }


    public ASVLOFFSCREEN loadImage(File file) {
        ASVLOFFSCREEN inputImg = new ASVLOFFSCREEN();
        if (bUseBGRToEngine) {
            BufferInfo bufferInfo = ImageLoader.getBGRFromFile(file);
            inputImg.u32PixelArrayFormat = ASVL_COLOR_FORMAT.ASVL_PAF_RGB24_B8G8R8;
            inputImg.i32Width = bufferInfo.width;
            inputImg.i32Height = bufferInfo.height;
            inputImg.pi32Pitch[0] = inputImg.i32Width * 3;
            if(inputImg.pi32Pitch[0] != 0 && inputImg.i32Height != 0){
                inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
                inputImg.ppu8Plane[0].write(0, bufferInfo.buffer, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
            } else{
                inputImg.ppu8Plane[0] = Pointer.NULL;
            }
            inputImg.ppu8Plane[1] = Pointer.NULL;
            inputImg.ppu8Plane[2] = Pointer.NULL;
            inputImg.ppu8Plane[3] = Pointer.NULL;
        } else {
            BufferInfo bufferInfo = ImageLoader.getI420FromFile(file);
            inputImg.u32PixelArrayFormat = ASVL_COLOR_FORMAT.ASVL_PAF_I420;
            inputImg.i32Width = bufferInfo.width;
            inputImg.i32Height = bufferInfo.height;
            inputImg.pi32Pitch[0] = inputImg.i32Width;
            inputImg.pi32Pitch[1] = inputImg.i32Width / 2;
            inputImg.pi32Pitch[2] = inputImg.i32Width / 2;
            inputImg.ppu8Plane[0] = new Memory(inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[0].write(0, bufferInfo.buffer, 0, inputImg.pi32Pitch[0] * inputImg.i32Height);
            inputImg.ppu8Plane[1] = new Memory(inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[1].write(0, bufferInfo.buffer, inputImg.pi32Pitch[0] * inputImg.i32Height, inputImg.pi32Pitch[1] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[2] = new Memory(inputImg.pi32Pitch[2] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[2].write(0, bufferInfo.buffer, inputImg.pi32Pitch[0] * inputImg.i32Height + inputImg.pi32Pitch[1] * inputImg.i32Height / 2, inputImg.pi32Pitch[2] * inputImg.i32Height / 2);
            inputImg.ppu8Plane[3] = Pointer.NULL;
        }

        inputImg.setAutoRead(false);
        return inputImg;
    }
}
