package com.base.web.bean.po.organization;

import com.base.web.bean.po.GenericPo;

/**
 * 后台组织管理模块
 * Created by FQ
 */
public class Organization extends GenericPo<Long> {

    //组织编码
    private String code;

    //组织名称
    private String name;

    //父id
    private String parentId;

    //等级
    private Integer level;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public interface Property extends GenericPo.Property {
        String code = "code";
        String name = "name";
        String parentId = "parentId";
    }
}
