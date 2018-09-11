package com.base.web.service.impl.resource;

import com.base.web.bean.po.resource.FileRef;
import com.base.web.core.utils.WebUtil;
import com.base.web.dao.resource.FileRefMapper;
import com.base.web.service.impl.AbstractServiceImpl;
import com.base.web.service.resource.FileRefService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Service("FileRefService")
public class FileRefServiceImpl extends AbstractServiceImpl<FileRef, Long> implements FileRefService {

    @Resource
    protected FileRefMapper fileRefMapper;


    //默认数据映射接口
    @Override
    protected FileRefMapper getMapper() {
        return this.fileRefMapper;
    }


    public static String resourceBuildPath(HttpServletRequest req, String md5) {
        StringBuffer sb = new StringBuffer();
        sb.append(WebUtil.getBasePath(req)).append("file/image/").append(md5).append(".jpg");
        return sb.toString();
    }

    public static String resourceBuildPath(HttpServletRequest req, String md5, String type) {
        StringBuffer sb = new StringBuffer();
        sb.append(WebUtil.getBasePath(req)).append("file/download/").append(md5).append(type);
        return sb.toString();
    }

}
