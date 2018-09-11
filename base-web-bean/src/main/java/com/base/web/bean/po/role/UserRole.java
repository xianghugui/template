package com.base.web.bean.po.role;

import org.hibernate.validator.constraints.NotEmpty;
import com.base.web.bean.po.GenericPo;

/**
 * 后台管理用户角色绑定
 * Created by generator
 */
public class UserRole extends GenericPo<Long> {
    private static final long serialVersionUID = 8910856253780046561L;

    //用户主键
    @NotEmpty
    private Long userId;

    //角色主键
    @NotEmpty
    private Long roleId;

    //角色实例
    private transient Role role;

    /**
     * 获取 用户主键
     *
     * @return String 用户主键
     */
    public Long getUserId() {
        if (this.userId == null)
            return Long.valueOf(0);
        return this.userId;
    }

    /**
     * 设置 用户主键
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取 角色主键
     *
     * @return String 角色主键
     */
    public Long getRoleId() {
        if (this.roleId == null)
            return Long.valueOf(0);
        return this.roleId;
    }

    /**
     * 设置 角色主键
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public interface Property {
        /**
         * @see UserRole#userId
         */
        String userId = "userId";
        /**
         * @see UserRole#roleId
         */
        String roleId = "roleId";
        /**
         * @see UserRole#role
         */
        String role   = "role";
    }
}