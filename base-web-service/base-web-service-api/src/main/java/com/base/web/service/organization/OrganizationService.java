package com.base.web.service.organization;

import com.base.web.bean.po.organization.Organization;
import com.base.web.service.GenericService;

import java.util.List;
import java.util.Map;


/**
 * 组织管理模块服务类
 * Created by FQ
 */
public interface OrganizationService extends GenericService<Organization, Long> {

    /**
     * 查询设备在内的四级树
     * @return
     */
    List<Map> queryTree();

}
