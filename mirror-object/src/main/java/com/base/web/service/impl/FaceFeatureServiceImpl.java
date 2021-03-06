package com.base.web.service.impl;

import com.base.web.bean.FaceFeature;
import com.base.web.dao.FaceFeatureMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.FaceFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("faceFeatureService")
public class FaceFeatureServiceImpl extends AbstractServiceImpl<FaceFeature, Long> implements FaceFeatureService {
    @Autowired
    private FaceFeatureMapper faceFeatureMapper;

    @Override
    protected GenericMapper<FaceFeature, Long> getMapper() {
        return faceFeatureMapper;
    }
}
