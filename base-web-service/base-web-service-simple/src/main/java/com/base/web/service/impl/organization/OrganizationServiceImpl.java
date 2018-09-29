package com.base.web.service.impl.organization;

import com.base.web.bean.po.organization.Organization;
import com.base.web.dao.organization.OrganizationMapper;
import com.base.web.service.impl.AbstractServiceImpl;
import com.base.web.service.organization.OrganizationService;
import javafx.scene.Camera;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    public int delete(Long id) {
        getMapper().deleteNode(id);
        return 1;
    }

    @Override
    public List<Map> queryTree() {
        return organizationMapper.queryTree();
    }

    @Override
    public List<Map> listCameraByAreaId(Long areaId) {
        return organizationMapper.listCameraByAreaId(areaId);
    }

    @Override
    public List<Map> listCameraByAreaIdTotal(Long areaId) {
        return organizationMapper.listCameraByAreaIdTotal(areaId);
    }
}
