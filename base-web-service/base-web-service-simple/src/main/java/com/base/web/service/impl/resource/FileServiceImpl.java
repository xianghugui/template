package com.base.web.service.impl.resource;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.digest.DigestUtils;
import com.base.web.bean.po.resource.Resources;
import com.base.web.bean.po.user.User;
import com.base.web.core.exception.NotFoundException;
import com.base.web.service.config.ConfigService;
import com.base.web.service.resource.FileService;
import com.base.web.service.resource.ResourcesService;
import com.base.web.core.utils.WebUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hsweb.commons.DateTimeUtils;
import org.hsweb.commons.MD5;
import javax.annotation.Resource;
import java.io.*;
import java.util.Date;

@Service("FileService")
public class FileServiceImpl implements FileService {
    @Resource
    private ConfigService configService;
    @Resource
    private ResourcesService resourcesService;

    public String getFileBasePath() {
        return configService.get("upload", "basePath", "./upload").trim();
    }

    @Override
    public InputStream readResources(Long resourceId) throws IOException {
        Resources resources = resourcesService.selectByPk(resourceId);
        if (resources == null) throw new NotFoundException("文件不存在");
        return readResources(resources);
    }

    @Override
    public void deleteResources(Long resourceId) {
        Resources resources = resourcesService.selectByPk(resourceId);
        if (resources == null) throw new NotFoundException("文件不存在");
        deleteResources(resources);
    }

    @Override
    public InputStream readResources(Resources resources) throws IOException {
        String fileBasePath = getFileBasePath();
        File file = new File(fileBasePath.concat(resources.getPath().concat("/".concat(resources.getMd5()))));
        if (!file.canRead()) {
            throw new NotFoundException("文件不存在");
        }
        return new FileInputStream(file);
    }

    @Override
    public void deleteResources(Resources resources) {
        String fileBasePath = getFileBasePath();
        File file = new File(fileBasePath.concat(resources.getPath().concat("/".concat(resources.getMd5()))));
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void writeResources(Resources resources, OutputStream outputStream) throws IOException {
        try (InputStream inputStream = readResources(resources)) {
            byte b[] = new byte[2048 * 10];
            while ((inputStream.read(b)) != -1) {
                outputStream.write(b);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public Resources saveFile(InputStream is, String fileName) throws IOException {
        //配置中的文件上传根路径
        String fileBasePath = getFileBasePath();
        //文件存储的相对路径，以日期分隔，每天创建一个新的目录
        String filePath = "/file/".concat(DateTimeUtils.format(new Date(), DateTimeUtils.YEAR_MONTH_DAY));
        //文件存储绝对路径
        String absPath = fileBasePath.concat(filePath);
        File path = new File(absPath);
        if (!path.exists()) path.mkdirs(); //创建目录
        String newName = MD5.encode(String.valueOf(System.nanoTime())); //临时文件名 ,纳秒的md5值
        String fileAbsName = absPath.concat("/").concat(newName);
        //try with resource
        long fileLength = 0;
        fileLength = getFileLength(is, fileAbsName, fileLength);
        File newFile = new File(fileAbsName);
        //获取文件的md5值
        String md5;
        try (FileInputStream inputStream = new FileInputStream(newFile)) {
            md5 = DigestUtils.md5Hex(inputStream);
        }
        //判断文件是否已经存在
        Resources resources = resourcesService.selectByMd5(md5);
        if (resources != null) {
            newFile.delete();//文件已存在则删除临时文件不做处理
            return resources;
        } else {
            newName = md5;
            newFile.renameTo(new File(absPath.concat("/").concat(newName)));
        }
        resources = new Resources();
        resources.setPath(filePath);
        resources.setMd5(md5);
        resources.setType("file");
        resources.setSize(fileLength);
        resources.setName(fileName);
        try {
            User user = WebUtil.getLoginUser();
            if (user != null) {
                resources.setCreateId(user.getId());
            } else {
                resources.setCreateId(00001L);
            }
        } catch (Exception e) {
            resources.setCreateId(00001L);
        }
        resourcesService.insert(resources);
        return resources;
    }
//图片重复删除方法无效  需修改
    @Transactional(rollbackFor = Throwable.class)
    public Resources saveCompressFile(InputStream is, String fileName) throws IOException {
        //配置中的文件上传根路径
        String fileBasePath = getFileBasePath();
        //文件存储的相对路径，以日期分隔，每天创建一个新的目录
        String filePath = "/file/".concat("compress");
        //文件存储绝对路径
        String absPath = fileBasePath.concat(filePath);
        File path = new File(absPath);
        if (!path.exists()) path.mkdirs(); //创建目录
        //临时文件名 ,纳秒的md5值
        String newName = MD5.encode(String.valueOf(System.nanoTime()));
        String fileAbsName = absPath.concat("/").concat(newName);
        long fileLength = 0;
        fileLength = getFileLength(is, fileAbsName, fileLength);
        //try with resource
        File newFile = new File(fileAbsName);
        //获取文件的md5值
        String md5;
        try (FileInputStream inputStream = new FileInputStream(newFile)) {
            md5 = DigestUtils.md5Hex(inputStream);
        }
        //判断上传原文件是否已经存在
        Resources resources = resourcesService.selectByMd5(md5);
        if (resources != null) {
            //上传源文件保存绝对位置及相应文件名
            String original = absPath.concat("/").concat(newName);
            //压缩文件保存绝对位置及相应文件名
            String fileCompressAbsName = absPath.concat("/").concat("compress_" + newName);
            try {
                //压缩后文件带文件后缀名
                Thumbnails.of(original).scale(0.5f).toFile(fileCompressAbsName);

                File compressImage =  new File(fileCompressAbsName + ".png");
                System.out.println("file.length();" + compressImage.length());
                long fileCompressLength = 0;
                //获取压缩文件的md5值
                String md5Compress;
                try (FileInputStream inputStream = new FileInputStream(compressImage)) {
                    md5Compress = DigestUtils.md5Hex(inputStream);
                }
                //获取压缩文件长度
                try (FileInputStream inputStream = new FileInputStream(compressImage)) {
                    fileCompressLength = getFileLength(inputStream, fileAbsName, fileCompressLength);
                }
                //判断压缩文件是否存在
                Resources resourcesCompress = resourcesService.selectByMd5(md5Compress);
                if (resourcesCompress != null) {
                //压缩文件存在则删除文件系统当前压缩文件
                    compressImage.delete();
                    //存在则删除压缩文件并返回资源数据
                    return resourcesCompress;
                }else {
                    //如果压缩图片不存在，用图片MD5代替文件名并存储在项目运行系统相对目录下
                    newName = md5Compress;
                    //将压缩图片以压缩图片MD5值重命名
                    compressImage.renameTo(new File(absPath.concat("/").concat(newName)));
                    resources = new Resources();
                    resources.setMd5(md5Compress);
                    resources.setPath(filePath);
                    resources.setType("file");
                    resources.setSize(fileCompressLength);
                    resources.setCreateTime(new Date());
                    resources.setCreateId(WebUtil.getLoginUser().getId());
                    resources.setName("compress_" + fileName);
                    resourcesService.insert(resources);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //如果上传图片不存在，用图片MD5代替文件名并存储在项目运行系统相对目录下
            newName = md5;
            newFile.renameTo(new File(absPath.concat("/").concat(newName)));
            //上传文件保存绝对位置
            String original = absPath.concat("/").concat(newName);
            //压缩后图片存储绝对位置及文件名
            String fileCompressAbsName = absPath.concat("/").concat("compress_" + newName);
            try {
                //压缩后文件带文件后缀名
                Thumbnails.of(original).scale(0.5f).toFile(fileCompressAbsName);
                File compressImage =  new File(fileCompressAbsName + ".png");
                System.out.println("file.length();" + compressImage.length());
                long fileCompressLength = 0;
                //获取压缩后文件的md5值
                String md5Compress;
                try (FileInputStream inputStream = new FileInputStream(compressImage)) {
                    md5Compress = DigestUtils.md5Hex(inputStream);
                }
                //获取压缩文件的长度
                try (FileInputStream inputStream = new FileInputStream(compressImage)) {
                    fileCompressLength = getFileLength(inputStream, fileAbsName, fileCompressLength);
                }
                //判断当前压缩文件是否存在
                Resources resourcesCompress = resourcesService.selectByMd5(md5Compress);
                if (resourcesCompress != null) {
                    compressImage.delete();//文件已存在则删除临时文件不做处理
                    return resourcesCompress;
                }else {
                    //如果压缩图片不存在，用图片MD5代替文件名并存储在项目运行系统相对目录下
                    newName = md5Compress;
                    //将压缩图片以压缩图片MD5值重命名
                    compressImage.renameTo(new File(absPath.concat("/").concat(newName)));
                    resources = new Resources();
                    resources.setPath(filePath);
                    resources.setMd5(md5Compress);
                    resources.setType("file");
                    resources.setSize(fileCompressLength);
                    resources.setCreateTime(new Date());
                    resources.setCreateId(WebUtil.getLoginUser().getId());
                    resources.setName("compress_" + fileName);
                    resourcesService.insert(resources);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //服务器不存在上传图片，删除上传原图
            newFile.delete();
        }
        return resources;
    }
    /**
     * 获取上传资源长度
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
                fileLength+=len;
                os.write(buffer, 0, len);
            }
            os.flush();
        }
        return fileLength;
    }


    /**
     * 客户端登录用户登录上传个人正脸照片检测自己的试衣视频
     * @param imageInputStream
     * @param uploadImageFileName
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Throwable.class)
    public Resources saveUserUploadMatchImageFile(InputStream imageInputStream, String uploadImageFileName) throws IOException {

        //配置中的文件上传根路径
        String uploadImageFileBasePath = getFileBasePath();
        //文件存储的相对路径，以日期分隔，每天创建一个新的目录
        String imageFileStoragePath = "/file/".concat(DateTimeUtils.format(new Date(), DateTimeUtils.YEAR_MONTH_DAY));
        //文件存储绝对路径
        String imageFileShortagesPath = uploadImageFileBasePath.concat(imageFileStoragePath);
        File path = new File(imageFileShortagesPath);
        if (!path.exists()) path.mkdirs(); //创建目录
        String newName = MD5.encode(String.valueOf(System.nanoTime())); //临时文件名 ,纳秒的md5值
        String fileAbsName = imageFileShortagesPath.concat("/").concat(newName);
        //try with resource
        long fileLength = 0;
        fileLength = getFileLength(imageInputStream, fileAbsName, fileLength);
        File newFile = new File(fileAbsName);
        //获取文件的md5值
        String md5;
        try (FileInputStream inputStream = new FileInputStream(newFile)) {
            md5 = DigestUtils.md5Hex(inputStream);
        }
            newName = md5;
            newFile.renameTo(new File(imageFileShortagesPath.concat("/").concat(newName)));
        Resources   resources = new Resources();
        resources.setType("file");
        resources.setSize(fileLength);
        resources.setName(uploadImageFileName);
        resources.setPath(imageFileStoragePath);
        resources.setMd5(md5);

        try {
            User uploadImageFileUser = WebUtil.getLoginUser();
            if (uploadImageFileUser != null) {
                resources.setCreateId(uploadImageFileUser.getId());
            } else {
                resources.setCreateId(Long.valueOf(00001));
            }
        } catch (Exception e) {
            resources.setCreateId(Long.valueOf(00001));
        }
        resourcesService.insert(resources);
        return resources;
    }

    /**
     * 用户上传身份证正反面文件接口
     * @param is
     * @param fileName
     * @return
     * @throws IOException
     */
    @Transactional(rollbackFor = Throwable.class)
    public Resources saveUserIDCardFile(InputStream is, String fileName) throws IOException {
        //配置中的文件上传根路径
        String fileBasePath = getFileBasePath();
        //文件存储的相对路径，固定文件夹（Mirror-User-IDCard）
        String filePath = "/file/".concat("Mirror-User-IDCard");
        //文件存储绝对路径
        String absPath = fileBasePath.concat(filePath);
        File path = new File(absPath);
        if (!path.exists()) path.mkdirs(); //创建目录
        String newName = MD5.encode(String.valueOf(System.nanoTime())); //临时文件名 ,纳秒的md5值
        String fileAbsName = absPath.concat("/").concat(newName);
        //try with resource
        long fileLength = 0;
        fileLength = getFileLength(is, fileAbsName, fileLength);
        File newFile = new File(fileAbsName);
        //获取文件的md5值
        String md5;
        try (FileInputStream inputStream = new FileInputStream(newFile)) {
            md5 = DigestUtils.md5Hex(inputStream);
        }

        newName = md5;
        newFile.renameTo(new File(absPath.concat("/").concat(newName)));

        Resources resources = new Resources();

        resources.setPath(filePath);
        resources.setMd5(md5);
        resources.setType("file");
        resources.setSize(fileLength);
        resources.setName(fileName);
//        try {
//            TUser user = WebUtil.getLoginUser();
//            if (user != null) {
//                resources.setCreateId(user.getId());
//            } else {
                resources.setCreateId(00001L);
//            }
//        } catch (Exception e) {
//            resources.setCreateId(00001L);
//        }
        resourcesService.insert(resources);
        return resources;
    }
}
