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

    private List<AimsMessageDTO> saveFace;

    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    @AccessLogger("人脸检测")
    @Authorize(action = "C")
    public String faceRecognize(@RequestParam("file") MultipartFile file) throws Exception {
        String returnStr = "没有获取到特征值,请重新上传图片";
        if (file != null) {
            String currentImagePath;
            if (FaceFeatureUtil.isWin) {
                currentImagePath = System.getProperty("user.dir") + File.separator
                        + "upload" + File.separator + "face" + File.separator + file.getOriginalFilename();
            } else {
                currentImagePath = "/data/apache-tomcat-8.5.31/bin/upload/face/" + file.getOriginalFilename();
            }
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
    public ResponseMessage faceRecognize(UploadValue uploadValue, HttpServletRequest req) throws ExecutionException, InterruptedException {
        //模糊匹配组织ID
        String organizationId = uploadValue.getOrganizationId();
        if (organizationId != null) {
            uploadValue.setOrganizationId(organizationId + "%");
        }
        if (uploadValue.getUploadId() == null) {
            //上传文件没有检测到人脸直接返回全部人脸
            return ResponseMessage.ok(new ArrayList<>());
        } else {
            byte[] uploadFaceFeature = uploadFeatureService.selectByPk(uploadValue.getUploadId()).getFaceFeature();
            int faceImageListTotal = aimsMessageService.listAimsMessageTotal(uploadValue);
//            ForkJoinPool forkjoinPool = new ForkJoinPool();
//            RetrieveBlacklistThread task = new RetrieveBlacklistThread(0,faceImageListTotal, uploadFaceFeature, uploadValue);
//            Future<List<AimsMessageDTO>> result = forkjoinPool.submit(task);
//            forkjoinPool.shutdown();

            ExecutorService executor = Executors.newCachedThreadPool();

            for(int i = 0;i <= faceImageListTotal/1000;i++){
                uploadValue.setPageIndex(i*1000);
                uploadValue.setPageSize(i*1000+1000);
                executor.execute(new InnerThread(uploadValue,uploadFaceFeature));
            }
            executor.shutdown();
            return ResponseMessage.ok();
        }
    }


    @RequestMapping(value = "/faceimage", method = RequestMethod.GET)
    @AccessLogger("人脸检索")
    @Authorize(action = "R")
    public ResponseMessage listFaceImage(UploadValue uploadValue, HttpServletRequest req) {
        return ResponseMessage.ok(faceImageService.listFaceImage(uploadValue, req));
    }


    private class RetrieveBlacklistThread extends RecursiveTask<List<AimsMessageDTO>> {
        private int THRESHOLD = 100;
        private int start;
        private int end;
        private List<AimsMessageDTO> returnFaceList = new ArrayList<AimsMessageDTO>();
        private byte[] uploadFaceFeature;
        private UploadValue uploadValue;

        public RetrieveBlacklistThread(int start,int end, byte[] uploadFaceFeature, UploadValue uploadValue) {
            this.start = start;
            this.end = end;
            this.uploadFaceFeature = uploadFaceFeature;
            this.uploadValue = uploadValue;
        }

        @Override
        protected List<AimsMessageDTO> compute() {
            //当end与start之间的差小于threshold时，开始进行实际的累加
            if(end - start <= THRESHOLD){
                try {
                    returnFaceList = face(start, end);
                    return returnFaceList;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {//当end与start之间的差大于threshold，即要累加的数超过20个时候，将大任务分解成小任务
                int middle = (end - start)/2;
                RetrieveBlacklistThread left = new RetrieveBlacklistThread(start,middle, uploadFaceFeature, uploadValue);
                RetrieveBlacklistThread right = new RetrieveBlacklistThread(middle,end, uploadFaceFeature, uploadValue);
                //并行执行两个小任务
                left.fork();
                right.fork();
                //把两个小任务累加的结果合并起来
                returnFaceList.addAll(left.join());
                returnFaceList.addAll(right.join());
            }
            return returnFaceList;
        }

        public List<AimsMessageDTO> face(int pageIndex, int pageSize) throws Exception {
            uploadValue.setPageIndex(pageIndex);
            uploadValue.setPageSize(pageSize);
            Long start = System.currentTimeMillis();
            List<AimsMessageDTO> faceImageList = aimsMessageService.listAimsMessage(uploadValue);
            System.out.println("time:"+(System.currentTimeMillis() - start) * 1.0 / 1000);
            //遍历匹配数据库的特征值
            List<FaceFeature> faceFeatureList;
            for (int i = 0; i < faceImageList.size(); ) {
                //查询当前图片包含的所有人脸特征值
                faceFeatureList = faceImageList.get(i).getList();
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

    class InnerThread implements Runnable{
        private UploadValue uploadValue;
        private byte[] uploadFaceFeature;

        public InnerThread(UploadValue uploadValue,byte[] uploadFaceFeature){
            this.uploadValue = uploadValue;
            this.uploadFaceFeature = uploadFaceFeature;
        }

        @Override
        public void run() {
            Long start = System.currentTimeMillis();
            List<AimsMessageDTO> faceImageList = aimsMessageService.listAimsMessage(uploadValue);
            //遍历匹配数据库的特征值
            List<FaceFeature> faceFeatureList;
            for (int i = 0; i < faceImageList.size(); ) {
                //查询当前图片包含的所有人脸特征值
                faceFeatureList = faceImageList.get(i).getList();
                if (faceFeatureList.size() == 0) {
                    faceImageList.remove(i);
                } else {
                    for (int k = 0; k < faceFeatureList.size(); k++) {
                        //检测成功之后跳出当前寻缓
                        Float similarity = null;
                        try {
                            similarity = FaceFeatureUtil.ENGINEMAPS.get(0L).compareFaceSimilarity(uploadFaceFeature, faceFeatureList.get(k).getFaceFeature());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
            System.out.println("select time:"+(System.currentTimeMillis() - start) * 1.0 / 1000);
            returnFaceList(faceImageList);
        }

        public List<AimsMessageDTO> returnFaceList(List<AimsMessageDTO> faceImageList){
            return faceImageList;
        }


    }

}
