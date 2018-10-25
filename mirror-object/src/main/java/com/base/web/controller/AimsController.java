package com.base.web.controller;


import com.base.web.bean.*;
import com.base.web.bean.po.GenericPo;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.*;
import com.base.web.util.FaceFeatureUtil;
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
            if (FaceFeatureUtil.isWin) {
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
            Future<List<AimsMessageDTO>> result = forkjoinPool.submit(task);
            //关闭线程池
            forkjoinPool.shutdown();
            return ResponseMessage.ok(result.get());
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
                try {
                    returnFaceList = face(start, end);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                int middle = start+(end - start)/2;
                RetrieveBlacklistThread left = new RetrieveBlacklistThread(start,middle, uploadFaceFeature, uploadValue);
                RetrieveBlacklistThread right = new RetrieveBlacklistThread(middle,end - middle, uploadFaceFeature, uploadValue);
                invokeAll(left, right);
                returnFaceList = left.join();
                returnFaceList.addAll(right.join());
            }
            return returnFaceList;
        }

        public List<AimsMessageDTO> face(int pageIndex, int pageSize) throws Exception {

            uploadValue.setPageIndex(pageIndex);
            uploadValue.setPageSize(pageSize);
            List<AimsMessageDTO> faceImageList = aimsMessageService.listAimsMessage(uploadValue);
            List<FaceFeature> faceFeatureList;
            //遍历匹配数据库的特征值
            for (int i = 0; i < faceImageList.size(); ) {
                //查询当前图片包含的所有人脸特征值
                faceFeatureList = faceImageList.get(i).getList();
                //特征不匹配,则删除list中的记录
                if (faceFeatureList.size() == 0) {
                    faceImageList.remove(i);
                } else {
                    for (int k = 0; k < faceFeatureList.size(); k++) {
                        //检测成功之后跳出当前寻缓
                        Float similarity = FaceFeatureUtil.ENGINEMAPS.get(0L).compareFaceSimilarity(uploadFaceFeature, faceFeatureList.get(k).getFaceFeature());
                        if (similarity >= uploadValue.getMinSimilarity()) {
                            faceImageList.get(i).setSimilarity(similarity);
                            i++;
                            continue;
                        } else if (k + 1 == faceFeatureList.size()) {
                            faceImageList.remove(i);
                        }
                    }
                }
            }
            return faceImageList;
        }
    }

}
