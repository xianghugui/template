package com.base.web.controller;


import com.base.web.bean.*;
import com.base.web.bean.po.GenericPo;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.*;
import com.base.web.util.FaceFeatureUtil;
import com.base.web.util.NetDvrInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/aims")
@AccessLogger("目标查询")
@Authorize(module = "aims")
public class AimsController extends GenericController<FaceImage, Long> {


    @Autowired
    private FaceImageService faceImageService;

    @Autowired
    private UploadFeatureService uploadFeatureService;

    @Autowired
    private AimsMessageService aimsMessageService;

    @Override
    protected FaceImageService getService() {
        return faceImageService;
    }


    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @AccessLogger("人脸检测")
    @Authorize(action = "C")
    public String faceRecognize(@RequestParam("file") MultipartFile file) throws Exception {
        String returnStr = "没有获取到特征值,请重新上传图片";
        if (file != null) {
            String currentImagePath;
            if (NetDvrInit.isWin) {
                currentImagePath = System.getProperty("user.dir") + File.separator
                        + "upload" + File.separator + "face" + File.separator;
            } else {
                currentImagePath = "/data/apache-tomcat-8.5.31/bin/upload/face/";
            }
            File path = new File(currentImagePath);
            //判断目录是否存在
            if (!path.exists()) {
                path.mkdirs();
            }
            currentImagePath += file.getOriginalFilename().split("[.]")[1];
            //创建临时目录
            File faceFile = new File(currentImagePath);
            file.transferTo(faceFile);

            //获取人脸特征值
            byte[][] bytes = FaceFeatureUtil.ENGINEMAPS.get(0L).returnFaceFeature(faceFile);
            //删除图片
            faceFile.delete();

            if ( bytes != null && bytes.length == 1) {
                UploadFeature uploadFeature = new UploadFeature();
                uploadFeature.setFaceFeature(bytes[0]);
                uploadFeature.setId(GenericPo.createUID());
                uploadFeature.setCreateTime(new Date());
                //插入人脸特征值
                return uploadFeatureService.insert(uploadFeature).toString();
            } else if (bytes != null && bytes.length > 1) {
                returnStr = "检测到多张人脸,请重新上传图片";
            }
        }
        return returnStr;
    }


    @RequestMapping(value = "/faceRecognize", method = RequestMethod.GET)
    @AccessLogger("目标查询")
    @Authorize(action = "R")
    public ResponseMessage faceRecognize(UploadValue uploadValue) throws ExecutionException, InterruptedException {
        //模糊匹配组织ID
        String organizationId = uploadValue.getOrganizationId();
        if (organizationId != null) {
            uploadValue.setOrganizationId(organizationId + "%");
        }
        if (uploadValue.getUploadId() == null) {
            return ResponseMessage.ok(new ArrayList<>());
        } else {
            byte[] uploadFaceFeature = uploadFeatureService.selectByPk(uploadValue.getUploadId()).getFaceFeature();
            //查询数据总条数
            int faceImageListTotal = aimsMessageService.listAimsMessageTotal(uploadValue);
            //创建线程池
            ForkJoinPool forkjoinPool = new ForkJoinPool();
            RetrieveBlacklistThread task = new RetrieveBlacklistThread(0,faceImageListTotal, uploadFaceFeature, uploadValue);
            List<AimsMessageDTO> result = forkjoinPool.invoke(task);
            //关闭线程池
            forkjoinPool.shutdown();
            return ResponseMessage.ok(result);
        }
    }


    @RequestMapping(value = "/faceimage", method = RequestMethod.GET)
    @AccessLogger("人脸检索")
    @Authorize(action = "R")
    public ResponseMessage listFaceImage(UploadValue uploadValue, HttpServletRequest req) {
        return ResponseMessage.ok(faceImageService.listFaceImage(uploadValue, req));
    }


    private class RetrieveBlacklistThread extends RecursiveTask<List<AimsMessageDTO>> {
        private int start;
        private int end;
        private byte[] uploadFaceFeature;
        private UploadValue uploadValue;

        /**
         * @param start   数据查询开始下标
         * @param end   数据查询结束下标
         * @param uploadFaceFeature 上传的人脸特征值
         * @param uploadValue  上传的数据筛选参数
         */
        public RetrieveBlacklistThread(int start,int end, byte[] uploadFaceFeature, UploadValue uploadValue) {
            this.start = start;
            this.end = end;
            this.uploadFaceFeature = uploadFaceFeature;
            this.uploadValue = uploadValue;
        }

        @Override
        protected List<AimsMessageDTO> compute() {
            List<AimsMessageDTO> returnFaceList = new ArrayList<>();
            //当end与start之间的差小于threshold时，返回人脸对比结果
            if(end - start <= 4000){
                uploadValue.setPageIndex(start);
                uploadValue.setPageSize(end - start);
                try {
                    returnFaceList = face(UploadValue.copyUploadValue(uploadValue));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                int middle = start+(end - start)/2;
                RetrieveBlacklistThread left = new RetrieveBlacklistThread(start, middle, uploadFaceFeature, uploadValue);
                RetrieveBlacklistThread right = new RetrieveBlacklistThread(middle, end, uploadFaceFeature, uploadValue);
                invokeAll(left, right);
                returnFaceList = left.join();
                returnFaceList.addAll(right.join());
            }
            return returnFaceList;
        }

        public List<AimsMessageDTO> face(UploadValue uploadValue) {
            List<AimsMessageDTO> faceImageList = aimsMessageService.listAimsMessage(uploadValue);
            FaceFeatureUtil faceFeatureUtil = new FaceFeatureUtil();
            faceImageList = faceImageList.stream().filter(aimsMessageDTO -> {
                List<FaceFeature> faceFeatureList = aimsMessageDTO.getList();
                if (faceFeatureList != null) {
                    for (int i = 0; i < faceFeatureList.size(); i++) {
                        Float similarity;
                        try {
                            similarity = faceFeatureUtil.compareFaceSimilarity(uploadFaceFeature, faceFeatureList.get(i).getFaceFeature());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                        if (similarity >= uploadValue.getMinSimilarity()) {
                            aimsMessageDTO.setSimilarity(similarity);
                            return true;
                        }
                    }
                }
                return false;
            }).collect(Collectors.toList());
            faceFeatureUtil.clearFaceEngine();
            return faceImageList;
        }
    }

}
