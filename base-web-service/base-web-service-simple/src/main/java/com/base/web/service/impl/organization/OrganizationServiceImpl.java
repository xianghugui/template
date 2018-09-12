package com.base.web.service.impl.organization;

import com.base.web.bean.po.organization.Organization;
import com.base.web.dao.organization.OrganizationMapper;
import com.base.web.service.impl.AbstractServiceImpl;
import com.base.web.service.organization.OrganizationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("organizationService")
public class OrganizationServiceImpl extends AbstractServiceImpl<Organization, Long> implements OrganizationService {

    //默认数据映射接口
    @Resource
    protected OrganizationMapper organizationMapper;

    @Override
    protected OrganizationMapper getMapper() {
        return this.organizationMapper;
    }

    @Override
    public Long insert(Organization data) {
        return super.insert(data);
    }

    @Override
    public int update(Organization data) {
        return super.update(data);
    }

    @Override
    public int delete(Long id) {
        getMapper().deleteNode(id);
        return 1;
    }

}
