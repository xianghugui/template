package com.base.web.service.impl.module;

import com.base.web.bean.po.module.Module;
import com.base.web.dao.module.ModuleMapper;
import com.base.web.service.impl.AbstractServiceImpl;
import com.base.web.service.module.ModuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("moduleService")
public class ModuleServiceImpl extends AbstractServiceImpl<Module, String> implements ModuleService {

    //默认数据映射接口
    @Resource
    protected ModuleMapper moduleMapper;

    @Override
    protected ModuleMapper getMapper() {
        return this.moduleMapper;
    }

}
