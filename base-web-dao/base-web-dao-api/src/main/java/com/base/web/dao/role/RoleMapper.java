package com.base.web.dao.role;

import com.base.web.dao.GenericMapper;
import com.base.web.bean.po.role.Role;

import java.util.List;
import java.util.Map;

/**
* 后台管理角色数据映射接口
* Created by generator 
*/
public interface RoleMapper extends GenericMapper<Role,Long> {


    List<Map> selectRole();

    List<Map>  selectBrandUserQueryRole();
}
