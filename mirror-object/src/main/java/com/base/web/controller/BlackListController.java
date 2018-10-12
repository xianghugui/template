package com.base.web.controller;

import com.base.web.bean.BlackList;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.bean.po.resource.Resources;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.BlackListService;
import com.base.web.service.FaceImageService;
import com.base.web.service.resource.FileService;
import com.base.web.service.resource.ResourcesService;
import com.base.web.util.FaceFeatureUtil;
import com.base.web.util.ResourceUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    private FileService fileService;

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
            list.get(i).setFaceFeature(null);
        }
        return ResponseMessage.ok(new PagerResult(list.size(), list));
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @AccessLogger("添加黑名单")
    @Authorize(action = "C")
    public String insert(@RequestParam("file") MultipartFile file, BlackList blackList) throws Exception {
        if (file == null) {
            return "-1";
        }
        blackList = saveImage(file, blackList);
        if (blackList == null) {
            return "-1";
        } else {
            blackList.setCreateTime(new Date());
            return blackListService.insert(blackList).toString();
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @AccessLogger("更新黑名单")
    @Authorize(action = "U")
    public ResponseMessage update(@PathVariable("id") Long id, @RequestParam(value = "file", required = false) MultipartFile file,
                         BlackList blackList) throws Exception {
        BlackList oldBlackList = blackListService.selectByPk(id);
        assertFound(oldBlackList, "data is not found!");
        if (file != null) {
            blackList = saveImage(file, blackList);
        }
        blackListService.update(blackList);
        if (file != null) {
            deleteImage(oldBlackList.getResourceId());
        }
        return ResponseMessage.ok("更新成功");
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @AccessLogger("删除黑名单")
    @Authorize(action = "U")
    public ResponseMessage delete(@PathVariable("id") Long id) {
        BlackList oldBlackList = blackListService.selectByPk(id);
        assertFound(oldBlackList, "data is not found!");
        blackListService.delete(id);
        deleteImage(oldBlackList.getResourceId());
        return ResponseMessage.ok("删除成功");
    }

    /**
     * 如果图片没有被黑名单引用则删除
     *
     * @param resourceId
     */
    public void deleteImage(Long resourceId) {
        int total = blackListService.createQuery().where(BlackList.Property.resourceId, resourceId).total();
        if (total == 0) {
            Resources resources = resourcesService.selectByPk(resourceId);
            if (resources != null) {
                String fileBasePath = fileService.getFileBasePath();
                File file = new File(fileBasePath.concat(resources.getPath().concat("/".concat(resources.getMd5()))));
                file.delete();
                resourcesService.delete(resourceId);
            }
        }
    }

    /**
     * 保存图片以及特征值
     *
     * @param file
     * @return
     * @throws IOException
     */
    public BlackList saveImage(MultipartFile file, BlackList blackList) throws IOException {
        if (file == null) {
            return null;
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
        String fileName = file.getOriginalFilename();
        fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
        fileLength = getFileLength(file.getInputStream(), currentImagePath + fileName, fileLength);
        File faceFile = new File(currentImagePath + fileName);
        //获取人脸特征值
        Map<Integer, byte[]> map = faceFeatureUtil.returnFaceFeature(faceFile);
        if (map.size() != 1) {
            //没有检测到人脸
            faceFile.delete();
            return null;
        } else {
            //根据MD5命名
            String md5;
            try (FileInputStream inputStream = new FileInputStream(faceFile)) {
                md5 = DigestUtils.md5Hex(inputStream);
            }
            File newFile = new File(currentImagePath.concat(md5));
            faceFile.renameTo(newFile);
            Resources resources = new Resources();
            resources.setType("file");
            resources.setSize(fileLength);
            resources.setName(md5);
            resources.setPath("/blacklist/");
            resources.setMd5(md5);
            resources.setCreateTime(new Date());
            resourcesService.insert(resources);
            blackList.setFaceFeature(map.get(0));
            blackList.setResourceId(resources.getId());
            return blackList;
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
