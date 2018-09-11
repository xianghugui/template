package com.base.web.controller.role;

import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.bean.common.QueryParam;
import com.base.web.bean.po.role.Role;
import com.base.web.controller.GenericController;
import com.base.web.core.message.ResponseMessage;
import com.base.web.core.utils.WebUtil;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.base.web.service.role.RoleService;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;


import static com.base.web.core.message.ResponseMessage.ok;

/**
 * 后台管理角色控制器，继承自GenericController,使用rest+json
 * Created by generator 2015-8-26 10:57:38
 */
@RestController
@RequestMapping(value = "/role")
@AccessLogger("角色管理")
@Authorize(module = "role")
public class RoleController extends GenericController<Role, Long> {

    //默认服务类
    @Resource
    private RoleService roleService;

    @Override
    public RoleService getService() {
        return this.roleService;
    }

    @Override
    public ResponseMessage list(QueryParam param) {
        return super.list(param).exclude(Role.class, "modules");
    }


    @RequestMapping(value = "/roleList",method = RequestMethod.GET)
    @AccessLogger("查询列表")
    @Authorize(action = "R")
    public ResponseMessage listRole(QueryParam param) {
        // 获取条件查询
        Object data = null;
        if (!param.isPaging()){
            data = getService().QueryRoleList(WebUtil.getLoginUser().getId());
        }

//        else
//            data = getService().selectPager(param);
        return ok(data)
                .include(getPOType(), param.getIncludes())
                .exclude(getPOType(), param.getExcludes())
                .onlyData();
    }
}
