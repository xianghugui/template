/*
 * Copyright 2015-2016 http://base.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.base.web.controller.login;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.hsweb.commons.MD5;
import com.base.web.bean.po.user.User;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.exception.AuthorizeForbiddenException;
import com.base.web.core.exception.NotFoundException;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.core.session.HttpSessionManager;
import com.base.web.core.utils.WebUtil;
import com.base.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 授权控制器,用于登录系统
 */
@RestController
public class AuthorizeController {

    /**
     * 授权过程所需缓存
     */
    @Autowired(required = false)
    private CacheManager cacheManager;

    /**
     * 用户服务类
     */
    @Resource
    private UserService userService;



    /**
     * httpSession管理器
     */
    @Autowired
    private HttpSessionManager httpSessionManager;

    /**
     * 获取当前在线人数
     *
     * @return 当前在线人数
     */
    @RequestMapping(value = "/online/total", method = RequestMethod.GET)
    @AccessLogger("当前在线总人数")
    @Authorize
    public ResponseMessage onlineTotal() {
        return ResponseMessage.ok(httpSessionManager.getUserTotal());
    }

    /**
     * 获取当前在线用户ID集合
     *
     * @return 在线用户ID集合
     */
    @RequestMapping(value = "/online", method = RequestMethod.GET)
    @AccessLogger("当前在线用户ID")
    @Authorize
    public ResponseMessage online() {
        return ResponseMessage.ok(httpSessionManager.getUserIdList());
    }

    /**
     * 获取当前在线用户信息集合
     *
     * @return 在线用户信息集合
     */
    @RequestMapping(value = "/online/list", method = RequestMethod.GET)
    @AccessLogger("当前在线用户")
    @Authorize
    public ResponseMessage onlineInfo() {
        return ResponseMessage.ok(httpSessionManager.tryGetAllUser())
                .include(User.class, "id", "username", "name", "phone", "email");
    }

    /**
     * 退出登录
     *
     * @return 退出成功
     */
    @RequestMapping(value = "/exit", method = RequestMethod.POST)
    @AccessLogger("登出")
    public ResponseMessage exit(HttpSession session) {
        User user = WebUtil.getLoginUser();
        if (user != null) {
            httpSessionManager.removeUser(user.getId());
            //使用redis时,有时候removeUser会失效,removeSession总可以了吧
            httpSessionManager.removeSession(session.getId());
        }
        return ResponseMessage.ok();
    }

    /**
     * 用户登录,如果密码输出错误,将会限制登录.

     *
     * @param username 用户名
     * @param password 密码
     * @param request  {@link HttpServletRequest}
     * @return 登录情况
     * @throws AuthorizeForbiddenException 用户被锁定或者密码错误
     * @throws NotFoundException           用户不存在或已注销
     * @throws Exception                   其他错误
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @AccessLogger("登录")
    public ResponseMessage login(@RequestParam String username,
                                 @RequestParam String password,
                                 HttpServletRequest request) throws Exception {
        //判断用户是否多次输入密码错误
        String userIp = WebUtil.getIpAddr(request);
        Cache cache = cacheManager.getCache("login.error");
        String cachePrefix = username.concat("@").concat(userIp);
        String timeCacheKey = cachePrefix.concat("-time");
        String numberCacheKey = cachePrefix.concat("-number");


        User user = userService.selectByUserName(username);
        if (user == null || user.getStatus() != 1) throw new NotFoundException("用户不存在或已注销");
        //密码错误

        cache.evict(timeCacheKey);
        cache.evict(numberCacheKey);
        user.setPassword("");//去除密码
        if (user.getUsername().equals("admin"))
            userService.initAdminUser(user);
        else
            user.initRoleInfo();
            user.setAvatar(WebUtil.getBasePath(request) + "img/Arnold.jpg");

        User newUser = new User();
        BeanUtilsBean.getInstance().getPropertyUtils()
                .copyProperties(newUser, user);
        httpSessionManager.addUser(newUser, request.getSession());
        return ResponseMessage.ok();
    }

    @PostConstruct
    public void init() {
        //如果系统没有配置cacheManager,则使用ConcurrentMapCacheManager
        if (cacheManager == null) {
            cacheManager = new ConcurrentMapCacheManager();
        }
    }

}
