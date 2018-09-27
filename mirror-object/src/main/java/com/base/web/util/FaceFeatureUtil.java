package com.base.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FaceFeatureUtil {

    /**
     * 检测上传图片的人脸特征值
     *
     * @param fileSrc
     * @return 特征值
     */
    public byte[] returnFaceFeature(String fileSrc){
        String arg[] = new String[]{"python3", "/home/FaceDetected/test/mytest.py",
                "/home/FaceDetected/test/images/a/01.jpg"};
        byte[] list = new byte[]{};
        try {
            Process pro = Runtime.getRuntime().exec(arg);
            BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            while((line = in.readLine()) != null) {
                System.out.println("检测上传图片的人脸特征值"+line);
//                list = line.getBytes();
            }
            in.close();
            pro.waitFor();
        }
        catch(IOException e){
            e.printStackTrace();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("上传图片"+fileSrc);
//        System.out.println("检测上传图片的人脸特征值"+list);
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
