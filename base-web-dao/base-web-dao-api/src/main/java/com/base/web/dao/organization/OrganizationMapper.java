package com.base.web.dao.organization;

import com.base.web.bean.po.organization.Organization;
import com.base.web.dao.GenericMapper;

/**
 * 组织管理模块数据映射接口
 * Created by FQ
 */
public interface OrganizationMapper extends GenericMapper<Organization,Long> {

    /**
     * 删除组织管理模块节点
     * @param id
     * @return
     */
    void deleteNode(Long id);
}
