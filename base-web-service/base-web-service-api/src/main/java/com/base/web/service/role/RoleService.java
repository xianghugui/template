package com.base.web.service.role;

import com.base.web.bean.po.role.Role;
import com.base.web.service.GenericService;
import java.util.List;
import java.util.Map;

/**
 * 后台管理角色服务类
 * Created by generator
 */
public interface RoleService extends GenericService<Role, Long> {

    List<Map> QueryRoleList(Long currentUserRole);

}
