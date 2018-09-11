package com.base.web.controller.user;

import com.base.web.bean.common.QueryParam;
import com.base.web.bean.po.user.User;
import com.base.web.controller.GenericController;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.core.utils.WebUtil;
import com.base.web.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.base.web.bean.po.user.User.Property.*;
import static com.base.web.core.message.ResponseMessage.created;


/**
 * 后台管理用户控制器，继承自GenericController,使用rest+json
 * Created by generator 2015-8-26 10:35:57
 */
@RestController
@RequestMapping(value = "/user")
@AccessLogger("用户管理")
@Authorize(module = "user")
public class UserController extends GenericController<User, Long> {

    //默认服务类
    @Resource
    private UserService userService;

    @Override
    public UserService getService() {
        return this.userService;
    }

    @Override
    public ResponseMessage list(QueryParam param) {
        param.excludes(password);
        return super.list(param)
                .exclude(User.class, password, modules, userRoles)
                .onlyData();
    }

    @Override
    public ResponseMessage info(@PathVariable("id") Long id) {
        return super.info(id).exclude(User.class, password, modules);
    }

    @AccessLogger("禁用")
    @RequestMapping(value = "/{id}/disable", method = RequestMethod.PUT)
    @Authorize(action = "disable")
    public ResponseMessage disable(@PathVariable("id") Long id) {
        getService().disableUser(id);
        return ResponseMessage.ok();
    }

    @AccessLogger("启用")
    @RequestMapping(value = "/{id}/enable", method = RequestMethod.PUT)
    @Authorize(action = "enable")
    public ResponseMessage enable(@PathVariable("id") Long id) {
        getService().enableUser(id);
        return ResponseMessage.ok();
    }
    @AccessLogger("删除")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    @Authorize(action = "D")
    public ResponseMessage delete(@PathVariable("id") Long id) {
        if (id ==WebUtil.getLoginUser().getId()){
            return ResponseMessage.error("管理员数据不允许删除");
        }else{
            getService().deleteUser(id);

        }
        return ResponseMessage.ok();
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @AccessLogger("新增")
    @Authorize(action = "C")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseMessage add(@RequestBody User object) {
        User user = getService().createQuery().where(User.Property.name,object.getUsername()).single();
        Long pk;
        if(user != null){
            return ResponseMessage.error("输入用户名重复");
        }else {
            object.setCreator(WebUtil.getLoginUser().getId());
            pk = getService().insert(object);
        }

        return created(pk);
    }

    @RequestMapping(value = "/addBrandUser/{userId}", method = RequestMethod.GET)
    @Authorize(action = "R")
    public ResponseMessage addBrandUser(@PathVariable("userId") String userId){
        return ResponseMessage.ok(getService().addBrandUser(userId));
    }

    @RequestMapping(value = "/allNoBelongBrandUser", method = RequestMethod.GET)
    @Authorize(action = "R")
    public ResponseMessage allNoBelongBrandUser(){
        return ResponseMessage.ok(getService().allNoBelongBrandUser());
    }
}
