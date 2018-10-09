package com.base.web.controller;


import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;
import com.base.web.bean.UploadFeature;
import com.base.web.bean.UploadValue;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.bean.po.GenericPo;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.FaceFeatureService;
import com.base.web.service.FaceImageService;
import com.base.web.service.UploadFeatureService;
import com.base.web.util.FaceFeatureUtil;
import com.base.web.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/aims")
@AccessLogger("目标查询")
@Authorize(module = "aims")
public class AimsController extends GenericController<FaceImage, Long> {
    @Autowired
    private FaceFeatureService faceFeatureService;

    @Autowired
    private FaceImageService faceImageService;

    @Autowired
    private FaceFeatureUtil faceFeatureUtil;

    @Autowired
    private UploadFeatureService uploadFeatureService;

    @Override
    protected FaceImageService getService() {
        return faceImageService;
    }

    private static final Boolean isWin = System.getProperty("os.name").toLowerCase().startsWith("win");

    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @AccessLogger("人脸检测")
    @Authorize(action = "C")
    public String faceRecognize(@RequestParam("file") MultipartFile file) throws Exception {
        String returnStr = "没有获取到特征值,请重新上传图片";
        if (file != null) {
            String currentImagePath;
            if (isWin) {
                currentImagePath = System.getProperty("user.dir") + File.separator
                        + "upload" + File.separator + "face" + File.separator + file.getOriginalFilename();
            } else {
                currentImagePath = "/data/apache-tomcat-8.5.31/bin/upload/face/" + file.getOriginalFilename();
            }
            //创建临时目录
            File faceFile = new File(currentImagePath);
            file.transferTo(faceFile);

            //获取人脸特征值
            Map<Integer, byte[]> map = faceFeatureUtil.returnFaceFeature(faceFile);
            //删除图片
            faceFile.delete();

            if (map.size() == 1) {
                UploadFeature uploadFeature = new UploadFeature();
                uploadFeature.setFaceFeature(map.get(0));
                uploadFeature.setId(GenericPo.createUID());
                //插入人脸特征值
                return uploadFeatureService.insert(uploadFeature).toString();
            }
            returnStr = "检测到多张人脸,请重新上传图片";
        }
        return returnStr;
    }


    @RequestMapping(value = "/faceRecognize", method = RequestMethod.GET)
    @AccessLogger("目标查询")
    @Authorize(action = "R")
    public ResponseMessage faceRecognize(UploadValue uploadValue, HttpServletRequest req) throws Exception {
        List<Map> faceImageList;

        //模糊匹配组织ID
        String organizationId = uploadValue.getOrganizationId();
        if (organizationId != null) {
            uploadValue.setOrganizationId(organizationId + "%");
        }

        if (uploadValue.getUploadId() == null) {
            //上传文件没有检测到人脸直接返回全部人脸
            faceImageList = faceImageService.queryAllFaceFeature(uploadValue);
            for (Map map : faceImageList) {
                map.put("imageUrl",
                        ResourceUtil.resourceBuildPath(req, map.get("resourceId").toString()));
            }
            return ResponseMessage.ok(faceImageList);

        } else {
            byte[] uploadFaceFeature = uploadFeatureService.selectByPk(uploadValue.getUploadId()).getFaceFeature();
            //获取数据库全部图片
            faceImageList = faceImageService.queryAllFaceFeature(uploadValue);
            //获取数据库的特征值
            for (int i = 0; i < faceImageList.size(); ) {
                //查询当前图片包含的所有人脸特征值
                List<FaceFeature> faceFeatureList = faceFeatureService.createQuery()
                        .where(FaceFeature.Property.faceImageId, faceImageList.get(i).get("resourceId"))
                        .list();

                if (faceFeatureList.size() == 0) {
                    faceImageList.remove(i);
                } else {
                    for (int k = 0; k < faceFeatureList.size(); k++) {
                        //检测成功之后跳出当前寻缓
                        Float similarity = faceFeatureUtil.compareFaceSimilarity(uploadFaceFeature, faceFeatureList.get(k).getFaceFeature());
                        if (similarity >= uploadValue.getMinSimilarity()) {
                            faceImageList.get(i).put("imageUrl",
                                    ResourceUtil.resourceBuildPath(req, faceImageList.get(i).get("resourceId").toString()));
                            faceImageList.get(i).put("similarity", similarity);
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


    @RequestMapping(value = "/faceimage", method = RequestMethod.GET)
    @AccessLogger("人脸检索")
    @Authorize(action = "R")
    public ResponseMessage listFaceImage(UploadValue uploadValue, HttpServletRequest req) {
        return ResponseMessage.ok(faceImageService.listFaceImage(uploadValue, req));
    }

}
