package com.base.web.dao.role;

import com.base.web.dao.GenericMapper;
import com.base.web.bean.po.role.RoleModule;

import java.util.List;

/**
 * 系统模块角色绑定数据映射接口
 * Created by generator
 */
public interface RoleModuleMapper extends GenericMapper<RoleModule, Long> {
    /**
     * 根据角色id查询
     *
     * @param roleId 角色id
     * @return
     */
    List<RoleModule> selectByRoleId(String roleId) ;

    int deleteByRoleId(Long roleId) ;

    int deleteByModuleId(String moduleId) ;
}
