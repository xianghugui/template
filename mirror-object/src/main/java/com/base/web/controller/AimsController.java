package com.base.web.controller;


import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.service.FaceFeatureService;
import com.base.web.service.FaceImageService;
import com.base.web.util.ResourceUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    @RequestMapping(value = "/uploadFaceImage", method = RequestMethod.POST,consumes = "multipart/form-data")
    @AccessLogger("上传图片")
    @Authorize(action = "C")
    public Map<String, Object> faceRecognize(@RequestParam("file") MultipartFile file,
                                             @RequestParam("deviceId")Long deviceId, HttpServletRequest req)  throws IOException {
        if (logger.isInfoEnabled())
            logger.info("start upload.");
        HashMap<String, Object> result = new HashMap<>();
        if (!file.isEmpty()) {
            if (logger.isInfoEnabled())
                logger.info("start write file:{}", file.getOriginalFilename());
            //获取数据库的特征值
            Byte[] uploadFaceFeature = new Byte[]{};
            List<FaceImage> faceImageList;
            if(uploadFaceFeature == null){
                //上传文件没有检测到人脸直接返回空数组
                faceImageList = null ;
            }else {
                //获取数据库的特征值
                faceImageList = faceImageService.createQuery().where(FaceImage.Property.deviceId,deviceId).list();
                for (int i = 0; i < faceImageList.size(); ) {
                    //查询当前视频对应图片包含的所有人脸特征值
                    List<FaceFeature> faceFeatureList = faceFeatureService.createQuery()
                            .where(FaceFeature.Property.faceImageId, faceImageList.get(i).getId())
                            .list();
                    if (faceFeatureList.size() == 0) {
                        faceImageList.remove(i);
                    } else {
                        for (int k = 0; k < faceFeatureList.size(); k++) {
                            //检测成功之后跳出当前寻缓
//                            if (uploadFaceFeature == faceFeatureList.get(k).getFaceFeature()) {
//                                faceImageList.get(i).put("imageUrl",
//                                        ResourceUtil.resourceBuildPath(req, faceImageList.get(i).getResourceId().toString()));
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
