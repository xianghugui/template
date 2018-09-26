package com.base.web.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FaceFeatureUtil {

    /**
     * 检测上传图片的人脸特征值
     *
     * @param fileSrc
     * @return 特征值
     */
    public byte[] returnFaceFeature(File fileSrc) {

        byte[] list = new byte[]{};
        return list;
    }

    /**
     * 人脸比较
     *
     * @param uploadFeature
     * @param recognizeFeature
     * @return 相识度
     */
    public Float faceRecognize(byte[] uploadFeature, byte[] recognizeFeature) {

        Float similarity = null;
        return similarity;
    }
}
