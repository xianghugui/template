package com.base.web.service.impl;

import com.base.web.bean.Camera;
import com.base.web.dao.CameraMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("cameraService")
public class CameraServiceImpl extends AbstractServiceImpl<Camera, Long> implements CameraService {

    @Autowired
    private CameraMapper cameraMapper;

    @Override
    protected GenericMapper<Camera, Long> getMapper() {
        return cameraMapper;
    }
}
