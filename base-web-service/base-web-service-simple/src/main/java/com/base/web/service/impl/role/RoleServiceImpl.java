package com.base.web.service.impl.role;

import com.base.web.bean.common.InsertParam;
import com.base.web.bean.po.GenericPo;
import com.base.web.bean.po.role.Role;
import com.base.web.bean.po.role.RoleModule;
import com.base.web.dao.role.RoleMapper;
import com.base.web.dao.role.RoleModuleMapper;
import com.base.web.service.impl.AbstractServiceImpl;
import com.base.web.service.module.ModuleService;
import com.base.web.service.role.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 后台管理角色服务类
 * Created by zh.sqy@qq.com
 */
@Service("roleService")
public class RoleServiceImpl extends AbstractServiceImpl<Role, Long> implements RoleService {

    //默认数据映射接口
    @Resource
    protected RoleMapper roleMapper;

    @Resource
    protected RoleModuleMapper roleModuleMapper;

    @Resource
    protected ModuleService moduleService;

    @Override
    protected RoleMapper getMapper() {
        return this.roleMapper;
    }

    @Override
    public Long insert(Role data) {
        Long id = super.insert(data);
        List<RoleModule> roleModule = data.getModules();
        if (roleModule != null && roleModule.size() > 0) {
            //保存角色模块关联
            for (RoleModule module : roleModule) {
                module.setId(GenericPo.createUID());
                module.setRoleId(data.getId());
                roleModuleMapper.insert(new InsertParam<>(module));
            }
        }
        return id;
    }

    @Override
    public int update(Role data){
        int l = super.update(data);
        List<RoleModule> roleModule = data.getModules();
        if (roleModule != null && roleModule.size() > 0) {
            //先删除所有roleModule
            roleModuleMapper.deleteByRoleId(data.getId());
            //保存角色模块关联
            for (RoleModule module : roleModule) {
                module.setId(GenericPo.createUID());
                module.setRoleId(data.getId());
                roleModuleMapper.insert(new InsertParam<>(module));
            }
        }
        return l;
    }

    @Override
    public List<Map> QueryRoleList(Long currentUserRole) {
        List<Map> list = new ArrayList<>();
        if(currentUserRole.equals("admin")){
            list =  getMapper().selectRole();
        }else {
            list = getMapper().selectBrandUserQueryRole();
        }
        return list;
    }
}
