package com.base.web.controller;


import com.base.web.bean.*;
import com.base.web.bean.po.GenericPo;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.*;
import com.base.web.util.FaceFeatureUtil;
import com.base.web.util.FmsgCallBack;
import com.base.web.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
            Long start = System.currentTimeMillis();
            //获取数据库全部图片
            List<AimsMessageDTO> faceImageList = aimsMessageService.listAimsMessage(uploadValue);
            System.out.println((System.currentTimeMillis() - start) * 1.0 / 1000);

//            ENGINE_POOL.execute(new AimsController.RetrieveBlacklistThread(faceImageList,uploadFaceFeature,uploadValue));

            Long start2 = System.currentTimeMillis();

            ForkJoinPool forkjoinPool = new ForkJoinPool();

            RetrieveBlacklistThread task = new RetrieveBlacklistThread(faceImageList, uploadFaceFeature, uploadValue);

            Future<List<AimsMessageDTO>> result = forkjoinPool.submit(task);

            System.out.println((System.currentTimeMillis() - start2) * 1.0 / 1000);

//            faceImageList = result.get();
            return ResponseMessage.ok();
        }
    }


    @RequestMapping(value = "/faceimage", method = RequestMethod.GET)
    @AccessLogger("人脸检索")
    @Authorize(action = "R")
    public ResponseMessage listFaceImage(UploadValue uploadValue, HttpServletRequest req) {
        return ResponseMessage.ok(faceImageService.listFaceImage(uploadValue, req));
    }

    /**
     * 人脸识别引擎线程池
     */
    private static final ExecutorService ENGINE_POOL = new ThreadPoolExecutor(0, 10,
            0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(),
            new ThreadFactory() {

                private final AtomicInteger mCount = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "Engine_pool:" + mCount.getAndIncrement());
                }
            });

    /**
     * 检索黑名单
     */


    private class RetrieveBlacklistThread extends RecursiveTask<List<AimsMessageDTO>> {

        private List<AimsMessageDTO> faceImageList;
        private List<AimsMessageDTO> returnFaceList;
        private byte[] uploadFaceFeature;
        private UploadValue uploadValue;

        public RetrieveBlacklistThread(List<AimsMessageDTO> faceImageList, byte[] uploadFaceFeature, UploadValue uploadValue) {
            this.faceImageList = faceImageList;
            this.uploadFaceFeature = uploadFaceFeature;
            this.uploadValue = uploadValue;
        }

        @Override
        public List<AimsMessageDTO> compute() {
            returnFaceList = new ArrayList<AimsMessageDTO>();
            if (faceImageList.size() <= 2000) {
                try {
                    return returnFaceList = face(0, faceImageList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                int end = 0;
                int start = 0;
                List<AimsMessageDTO> index;
                for (int i = 0; i < faceImageList.size() / 2000; i++) {
                    System.out.println(i);
                    end += 2000;
                    start = i * 2000;
                    index = faceImageList.subList(start,end);
                    RetrieveBlacklistThread retrieveBlacklistThread = new RetrieveBlacklistThread(index, uploadFaceFeature, uploadValue);
                    retrieveBlacklistThread.fork();
                    List<AimsMessageDTO> result = retrieveBlacklistThread.join();
                    returnFaceList.addAll(result);
                }
            }
            return returnFaceList;
        }

        public List<AimsMessageDTO> face(int i, int length) throws Exception {
            List<AimsMessageDTO> returnFace = new ArrayList<AimsMessageDTO>();
            //遍历匹配数据库的特征值
            List<FaceFeature> faceFeatureList;
            for (; i < faceImageList.size(); ) {
                //查询当前图片包含的所有人脸特征值
                faceFeatureList = faceImageList.get(i).getList();
                if (faceFeatureList.size() == 0) {
                    faceImageList.remove(i);
                } else {
                    for (int k = 0; k < faceFeatureList.size(); k++) {
                        //检测成功之后跳出当前寻缓
                        Float similarity = FaceFeatureUtil.ENGINEMAPS.get(0L).compareFaceSimilarity(uploadFaceFeature, faceFeatureList.get(k).getFaceFeature());
                        if (similarity >= uploadValue.getMinSimilarity()) {
//                            faceImageList.get(i).setImageUrl(ResourceUtil.resourceBuildPath(req, faceImageList.get(i).getResourceId().toString()));
                            faceImageList.get(i).setSimilarity(similarity);
                            i++;
                            break;
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
