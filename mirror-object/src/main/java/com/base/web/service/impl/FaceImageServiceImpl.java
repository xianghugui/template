package com.base.web.service.impl;

import com.base.web.bean.FaceImage;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.dao.FaceImageMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.FaceImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("faceImageService")
public class FaceImageServiceImpl extends AbstractServiceImpl<FaceImage, Long> implements FaceImageService {
    @Autowired
    private FaceImageMapper faceImageMapper;

    @Override
    protected GenericMapper<FaceImage, Long> getMapper() {
        return faceImageMapper;
    }

    @Override
    public PagerResult<Map> queryAllFaceImage(QueryParam param){
        PagerResult<Map> pagerResult = new PagerResult<>();
        int total = faceImageMapper.queryFaceImageTotal(param);
        pagerResult.setTotal(total);
        if (total == 0) {
            pagerResult.setData(new ArrayList<>());
        } else {
            pagerResult.setData(faceImageMapper.queryAllFaceImage(param));
        }
        return pagerResult;
    }
}
