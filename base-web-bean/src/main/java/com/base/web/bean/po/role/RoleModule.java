package com.base.web.bean.po.role;

import org.hibernate.validator.constraints.NotEmpty;
import com.base.web.bean.po.GenericPo;
import com.base.web.bean.po.module.Module;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统模块角色绑定
 * Created by generator
 */
public class RoleModule extends GenericPo<Long> {
    private static final long serialVersionUID = 8910856253780046561L;

    //模块主键
    @NotNull
    @NotEmpty
    private String moduleId;

    //角色主键
    @NotNull
    @NotEmpty
    private Long roleId;

    private List<String> actions;

    private transient Module module;


    /**
     * 获取 模块主键
     *
     * @return String 模块主键
     */
    public String getModuleId() {
        if (this.moduleId == null)
            return "";
        return this.moduleId;
    }

    /**
     * 设置 模块主键
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
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

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }



public interface Property extends GenericPo.Property{
	/**
	 *
	 * @see RoleModule#moduleId
	 */
	String moduleId="moduleId";
	/**
	 *
	 * @see RoleModule#roleId
	 */
	String roleId="roleId";
	/**
	 *
	 * @see RoleModule#actions
	 */
	String actions="actions";
	/**
	 *
	 * @see RoleModule#module
	 */
	String module="module";
	}
}