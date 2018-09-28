package com.base.web.controller;


import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.FaceFeatureService;
import com.base.web.service.FaceImageService;
import com.base.web.util.FaceFeatureUtil;
import com.base.web.util.ResourceUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/aims")
@AccessLogger("目标查询")
@Authorize(module = "aims")
public class AimsController extends GenericController<FaceImage, Long>{
    @Autowired
    private FaceFeatureService faceFeatureService;

    @Autowired
    private FaceImageService faceImageService;

    @Override
    protected FaceImageService getService() {
        return faceImageService;
    }

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("win");

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    @AccessLogger("查询")
    @Authorize(action = "R")
    public ResponseMessage select(QueryParam param, HttpServletRequest req) {
        PagerResult<Map> faceImageList = faceImageService.queryAllFaceImage(param,req);
        return ResponseMessage.ok(faceImageList)
                .include(getPOType(), param.getIncludes())
                .exclude(getPOType(), param.getExcludes())
                .onlyData();
    }

    @RequestMapping(value = "/uploadFaceImage", method = RequestMethod.POST)
    @AccessLogger("返回上传的人脸特征值")
    @Authorize(action = "C")
    public ResponseMessage returnFaceFeature(@RequestParam("file") MultipartFile file) throws IOException {
        FaceFeatureUtil faceFeatureUtil = new FaceFeatureUtil();
        String currentImagePath;
        if (isWin) {
            currentImagePath = System.getProperty("user.dir") + File.separator
                    + "upload" + File.separator + "face" + File.separator + file.getOriginalFilename();
        } else {
            currentImagePath = "/data/apache-tomcat-8.5.34/bin/upload/face/" + file.getOriginalFilename();
        }
        if (logger.isInfoEnabled())
            logger.info("start upload.");
        byte[] uploadFaceFeature = null;
        if (!file.isEmpty()) {
            if (logger.isInfoEnabled())
                logger.info("start write file:{}", file.getOriginalFilename());
            //获取该图片的特征值
            uploadFaceFeature = faceFeatureUtil.returnFaceFeature(currentImagePath);
        } else {
            return ResponseMessage.error("图片为空或数据加载失败，请重试！");
        }
        //响应上传成功的资源信息
        return ResponseMessage.ok(uploadFaceFeature);
    }


    @RequestMapping(value = "/faceRecognize/{uploadFaceFeature}", method = RequestMethod.POST)
    @AccessLogger("人脸检测")
    @Authorize(action = "R")
    public ResponseMessage faceRecognize(@PathVariable("uploadFaceFeature") String file,
                                         QueryParam param, HttpServletRequest req) throws IOException {
        List<Map> faceImageList;
        //获取数据库全部图片
        faceImageList = null;
        if (file.equals("undefined")) {
            //上传文件没有检测到人脸直接返回空数组
            return ResponseMessage.ok(faceImageService.queryAllFaceImage(param,req))
                    .include(getPOType(), param.getIncludes())
                    .exclude(getPOType(), param.getExcludes())
                    .onlyData();
        } else {
            FaceFeatureUtil faceFeatureUtil = new FaceFeatureUtil();
            byte[] uploadFaceFeature = file.getBytes();
            //获取数据库的特征值
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
                        if (faceFeatureUtil.faceRecognize(uploadFaceFeature, faceFeatureList.get(k).getFaceFeature()) > 0) {
                            faceImageList.get(i).put("imageUrl",
                                    ResourceUtil.resourceBuildPath(req, faceImageList.get(i).get("resourceId").toString()));
                            i++;
                            continue;
                        } else if (k + 1 == faceFeatureList.size()) {//匹配失败，从未检测列表中移除当前检测数据
                            faceImageList.remove(i);
                        }
                    }
                }
            }
        }
        //响应上传成功的资源信息
        return ResponseMessage.ok(faceImageList);
    }

}
