package com.base.web.service.impl;

import com.base.web.bean.FaceImage;
import com.base.web.dao.FaceImageMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.FaceImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("faceImageService")
public class FaceImageServiceImpl extends AbstractServiceImpl<FaceImage, Long> implements FaceImageService {
    @Autowired
    private FaceImageMapper faceImageMapper;

    @Override
    protected GenericMapper<FaceImage, Long> getMapper() {
        return faceImageMapper;
    }
}
