package com.base.web.controller;

import com.base.web.bean.BlackList;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.bean.po.resource.Resources;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.BlackListService;
import com.base.web.service.resource.ResourcesService;
import com.base.web.util.FaceFeatureUtil;
import com.base.web.util.ResourceUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/blacklist")
@AccessLogger("黑名单")
@Authorize(module = "blacklist")
public class BlackListController extends GenericController<BlackList, Long> {

    @Autowired
    private BlackListService blackListService;

    @Autowired
    private ResourcesService resourcesService;

    @Autowired
    private FaceFeatureUtil faceFeatureUtil;

    @Autowired
    private HttpServletRequest request;

    @Override
    @RequestMapping(method = RequestMethod.GET)
    @AccessLogger("查询列表")
    @Authorize(action = "R")
    public ResponseMessage list(QueryParam param) {
        List<BlackList> list = blackListService.select();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setImageUrl(ResourceUtil.resourceBuildPath(request, list.get(i).getResourceId().toString()));
        }
        return ResponseMessage.ok(new PagerResult(list.size(), list));
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @AccessLogger("添加黑名单")
    @Authorize(action = "C")
    public String faceRecognize(@RequestParam("file") MultipartFile file, BlackList blackList) throws Exception {
        if (file == null) {
            return "-1";
        }
        String currentImagePath;
        if (FaceFeatureUtil.isWin) {
            currentImagePath = System.getProperty("user.dir") + File.separator
                    + "upload" + File.separator + "blacklist" + File.separator;
        } else {
            currentImagePath = "/data/apache-tomcat-8.5.31/bin/upload/blacklist/";
        }
        File path = new File(currentImagePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        long fileLength = 0;
        fileLength = getFileLength(file.getInputStream(), currentImagePath + file.getOriginalFilename(), fileLength);
        File faceFile = new File(currentImagePath + file.getOriginalFilename());
        //获取人脸特征值
        Map<Integer, byte[]> map = faceFeatureUtil.returnFaceFeature(faceFile);
        if (map.size() < 1) {
            //没有检测到人脸
            faceFile.delete();
            return "-1";
        }else if(map.size() > 1) {
            //检测到多张人脸
            faceFile.delete();
            return "-2";
        }else{
            //根据MD5命名
            String md5;
            try (FileInputStream inputStream = new FileInputStream(faceFile)) {
                md5 = DigestUtils.md5Hex(inputStream);
            }
            Resources resources = resourcesService.selectByMd5(md5);
            if (resources != null) {
                faceFile.delete();
            } else {
                File newFile = new File(currentImagePath.concat(md5));
                faceFile.renameTo(newFile);
                resources = new Resources();
                resources.setType("file");
                resources.setSize(fileLength);
                resources.setName(md5);
                resources.setPath("/blacklist/");
                resources.setMd5(md5);
                resources.setCreateTime(new Date());
                resourcesService.insert(resources);
            }
            blackList.setFaceFeature(map.get(0));
            blackList.setResourceId(resources.getId());
            blackList.setCreateTime(new Date());
            return blackListService.insert(blackList).toString();
        }
    }

    /**
     * 获取上传资源长度
     *
     * @param is
     * @param fileAbsName
     * @param fileLength
     * @return
     * @throws IOException
     */
    private long getFileLength(InputStream is, String fileAbsName, long fileLength) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(is);
             BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(fileAbsName))) {
            byte[] buffer = new byte[2048 * 10];
            int len;
            while ((len = in.read(buffer)) != -1) {
                fileLength += len;
                os.write(buffer, 0, len);
            }
            os.flush();
        }
        return fileLength;
    }

    @Override
    protected BlackListService getService() {
        return this.blackListService;
    }
}
