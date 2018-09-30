package com.base.web.service.impl;

import com.base.web.bean.FaceFeature;
import com.base.web.bean.UploadFeature;
import com.base.web.dao.FaceFeatureMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.dao.UploadFeatureMapper;
import com.base.web.service.UploadFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("uploadFeatureService")
public class UploadFeatureServiceImpl extends AbstractServiceImpl<UploadFeature, Long> implements UploadFeatureService {
    @Autowired
    private UploadFeatureMapper uploadFeatureMapper;

    @Override
    protected GenericMapper<UploadFeature, Long> getMapper() {
        return uploadFeatureMapper;
    }
}
