package com.base.web.util;

import com.base.web.core.utils.WebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project: base-framework
 * Author: Sendya <18x@loacg.com>
 * Date: 2017/8/11 15:33
 */
public class ResourceUtil {

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

    /**
     * 获取用户头像工具
     * @param req
     * @param md5
     * @param gender
     * @return 用户头像，若没头像返回服务端默认设置的头像
     */
    public static String getUserAvatar(HttpServletRequest req, String md5, Integer gender) {
        StringBuilder sb = new StringBuilder();
        sb.append(WebUtil.getBasePath(req));
        // 检查用户头像是否存在
        if ( md5 == null || "".equals(md5)) {
            // check gender
            if (gender == 0) {
                sb.append("images/default/user-avatar0.jpg");
            } else {
                sb.append("images/default/user-avatar1.jpg");
            }
            return sb.toString();
        }

        return sb.toString();
    }

    /**
     * 如果当前用户没有头像，则设置默认头像
     * @param req
     * @param gender
     * @return
     */
    public static String getUserDefaultUserImg(HttpServletRequest req,  Integer gender) {
        StringBuilder sb = new StringBuilder();
        sb.append(WebUtil.getBasePath(req));
        // 检查用户头像是否存在
            // check gender
            if (gender == 0) {
                sb.append("images/default/user-avatar0.jpg");
            } else {
                sb.append("images/default/user-avatar1.jpg");
            }
            return sb.toString();
    }
    public static String getUserAvatar(HttpServletRequest req, String md5) {
        return ResourceUtil.getUserAvatar(req, md5, 0);
    }
}
