package com.base.web.dao.role;

import com.base.web.dao.GenericMapper;
import com.base.web.bean.po.role.UserRole;

import java.util.List;

/**
 * 后台管理用户角色绑定数据映射接口
 * Created by generator
 */
public interface UserRoleMapper extends GenericMapper<UserRole, Long> {
    /**
     * 根据用户id查询用户的角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     */
    List<UserRole> selectByUserId(Long userId);

    int deleteByUserId(Long userId);
}
