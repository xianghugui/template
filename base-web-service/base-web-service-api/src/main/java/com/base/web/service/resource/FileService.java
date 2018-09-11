package com.base.web.service.resource;

import com.base.web.bean.po.resource.Resources;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 文件服务接口，用于对服务器文件进行操作
 * Created by    on 2015-11-26 0026.
 */
public interface FileService {
    /**
     * 保存一个文件流，并返回保存后生成的资源对象
     *
     * @param is       文件输入流
     * @param fileName 文件原始名称
     * @return 生成的资源对象
     * @throws Exception 保存失败的异常信息
     */


    Resources saveFile(InputStream is, String fileName) throws IOException;

    /**
     * 保存压缩图片文件
     * @param is
     * @param fileName
     * @return
     * @throws IOException
     */
    Resources saveCompressFile(InputStream is, String fileName) throws IOException;

    InputStream readResources(Resources resources) throws IOException;

    InputStream readResources(Long resourceId) throws IOException;

    void deleteResources(Long resourceId) throws IOException;

    void deleteResources(Resources resources) throws IOException;

    void writeResources(Resources resources, OutputStream outputStream) throws IOException;

    String getFileBasePath();

    Resources saveUserUploadMatchImageFile(InputStream imageInputStream, String uploadImageFileName) throws IOException;

    Resources saveUserIDCardFile(InputStream is, String fileName) throws IOException;
}
