package com.base.web.dao.organization;

import com.base.web.bean.po.organization.Organization;
import com.base.web.dao.GenericMapper;
import javafx.scene.Camera;

import java.util.List;
import java.util.Map;

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

    /**
     * 查询设备在内的四级树
     * @return
     */
    List<Map> queryTree();

    /**
     * 根据区域ID查询摄像头
     * @param areaId
     * @return
     */
    List<Map> listCameraByAreaId(Long areaId);
    List<Map> listCameraByAreaIdTotal(Long areaId);
}
