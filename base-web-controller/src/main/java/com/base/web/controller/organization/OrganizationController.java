/*
 * Copyright 2015-2016 http://base.me
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.base.web.controller.organization;

import com.base.web.bean.common.PagerResult;
import com.base.web.bean.po.organization.Organization;
import com.base.web.controller.GenericController;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.organization.OrganizationService;
import javafx.scene.Camera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织管理模块(菜单)控制器,继承自{@link GenericController<Organization, Long>}
 *
 * @author
 * @since 1.0
 */
@RestController
@RequestMapping(value = "/organization")
@AccessLogger("组织管理模块管理")
@Authorize(module = "organization")
public class OrganizationController extends GenericController<Organization, Long> {

    @Resource
    private OrganizationService organizationService;

    @Override
    public OrganizationService getService() {
        return this.organizationService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @AccessLogger("新增节点")
    @Authorize(action = "C")
    @Override
    public ResponseMessage add(@RequestBody Organization data) {
        Integer idPrefix = 0;
        if (data.getParentId() == null) {
            idPrefix = getService().createQuery().where(Organization.Property.parentId, "-1").total();
            if (idPrefix == null || idPrefix == 0) {
                data.setId(100000000L);
            } else {
                Integer newId = idPrefix + 100;
                data.setId(newId * 1000000L);
            }
            data.setParentId("-1");
        } else {
            idPrefix = getService().createQuery().where(Organization.Property.parentId, data.getParentId()).total();
            DecimalFormat df = new DecimalFormat("000");
            String IdLevel = df.format(idPrefix+1);
            String newId;
            if (data.getLevel() == 0) {
                newId = Long.valueOf(data.getParentId()) / 1000000 + IdLevel + "000";
            } else {
                newId = Long.valueOf(data.getParentId()) / 1000 + IdLevel;
            }
            data.setId(Long.valueOf(newId));
        }
        return ResponseMessage.ok(getService().insert(data));
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @AccessLogger("删除节点")
    @Authorize(action = "D")
    @Override
    public ResponseMessage delete(@PathVariable("id") Long id) {
        return ResponseMessage.ok(getService().delete(id));
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @AccessLogger("修改节点")
    @Authorize(action = "U")
    public ResponseMessage update(@RequestBody Organization data) {
        return ResponseMessage.ok(getService().update(data));
    }

    @RequestMapping(value = "/organizationTree", method = RequestMethod.GET)
    @AccessLogger("查询列表")
    @Authorize(action = "R")
    public ResponseMessage listOrganization() {
        List<Organization> organizationList = organizationService.select();
        return ResponseMessage.ok(organizationList);
    }

    @RequestMapping(value = "/queryTree", method = RequestMethod.GET)
    @AccessLogger("查询设备在内的四级树")
    @Authorize(action = "R")
    public ResponseMessage queryTree() {
        List<Map> organizationList = organizationService.queryTree();
        return ResponseMessage.ok(organizationList).onlyData();
    }

    @GetMapping(value = "/{areaId}/camera")
    @AccessLogger("根据区域ID查询摄像头")
    @Authorize(action = "R")
    public ResponseMessage listCamera(@PathVariable("areaId") Long areaId) {
        return ResponseMessage.ok(organizationService.listCameraByAreaId(areaId));
    }
}
