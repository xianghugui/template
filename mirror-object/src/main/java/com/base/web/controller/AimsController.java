package com.base.web.controller;


import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.FaceFeatureService;
import com.base.web.service.FaceImageService;
import com.base.web.util.ResourceUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/aims")
@AccessLogger("目标查询")
@Authorize(module = "aims")
public class AimsController {
    @Autowired
    private FaceFeatureService faceFeatureService;

    @Autowired
    private FaceImageService faceImageService;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/uploadFaceImage", method = RequestMethod.POST, consumes = "multipart/form-data")
    @AccessLogger("返回上传的人脸特征值")
    @Authorize(action = "C")
    public ResponseMessage returnFaceFeature(@RequestParam("file") MultipartFile file) throws IOException {
        if (logger.isInfoEnabled())
            logger.info("start upload.");
        Byte[] uploadFaceFeature = null;
        if (!file.isEmpty()) {
            if (logger.isInfoEnabled())
                logger.info("start write file:{}", file.getOriginalFilename());
            //获取该图片的特征值
            uploadFaceFeature = new Byte[]{};
            String fileUrl = "E:\\feceTest";
            String fileName = "E:\\faceOut\\faceOut.json";
            try {
                String cmdStr_windows = "python E:\\IdeaProjects\\template\\mirror-object\\src\\main\\resources\\faceFeature\\mytest.py"+ " "
                        + "E:\\feceTest"+" "+fileName;
//                String[] args = new String[]{"python", "E:\\IdeaProjects\\template\\mirror-object\\src\\main\\resources\\faceFeature\\mytest.py", fileUrl};
                Process proc = Runtime.getRuntime().exec(cmdStr_windows);// 执行py文件
                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
                in.close();
                proc.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            return ResponseMessage.error("图片为空或数据加载失败，请重试！");
        }

        //响应上传成功的资源信息
        return ResponseMessage.ok(uploadFaceFeature);
    }


    @RequestMapping(value = "/faceRecognize", method = RequestMethod.POST, consumes = "multipart/form-data")
    @AccessLogger("上传图片")
    @Authorize(action = "R")
    public Map<String, Object> faceRecognize(@RequestParam("file") MultipartFile file,
                                             @RequestParam("deviceId") Long deviceId, HttpServletRequest req) throws IOException {
        if (logger.isInfoEnabled())
            logger.info("start upload.");
        HashMap<String, Object> result = new HashMap<>();
        if (!file.isEmpty()) {
            if (logger.isInfoEnabled())
                logger.info("start write file:{}", file.getOriginalFilename());
            //获取数据库的特征值
            Byte[] uploadFaceFeature = new Byte[]{};
            List<Map> faceImageList;
            if (uploadFaceFeature == null) {
                //上传文件没有检测到人脸直接返回空数组
                faceImageList = null;
            } else {
                //获取数据库的特征值
                faceImageList = faceImageService.queryAllFaceImage(deviceId);
                for (int i = 0; i < faceImageList.size(); ) {
                    //查询当前视频对应图片包含的所有人脸特征值
                    List<FaceFeature> faceFeatureList = faceFeatureService.createQuery()
                            .where(FaceFeature.Property.faceImageId, faceImageList.get(i).get("resourceId"))
                            .list();
                    if (faceFeatureList.size() == 0) {
                        faceImageList.remove(i);
                    } else {
                        for (int k = 0; k < faceFeatureList.size(); k++) {
                            //检测成功之后跳出当前寻缓
//                            if (uploadFaceFeature == faceFeatureList.get(k).getFaceFeature()) {
//                                faceImageList.get(i).put("imageUrl",
//                                        ResourceUtil.resourceBuildPath(req, faceImageList.get(i).get("resourceId").toString()));
//                                i++;
//                                continue;
//                            } else if (k + 1 == faceFeatureList.size()) {//匹配失败，从未检测列表中移除当前检测数据
//                                faceImageList.remove(i);
//                            }
                        }
                    }
                }
            }

        } else {
            String error = "图片为空或数据加载失败，请重试！";
        }

        //响应上传成功的资源信息
        return result;
    }

}
