package com.base.web.service.impl;

import com.base.web.bean.FaceImage;
import com.base.web.bean.UploadValue;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;
import com.base.web.dao.FaceImageMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.FaceImageService;
import com.base.web.util.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
    public PagerResult<Map> listFaceImage(UploadValue uploadValue, HttpServletRequest req) {
        PagerResult<Map> pagerResult = new PagerResult<>();
        int total = faceImageMapper.listFaceImageTotal(uploadValue);
        pagerResult.setTotal(total);
        if (total == 0) {
            pagerResult.setData(new ArrayList<>());
        } else {
            List<Map> list = faceImageMapper.listFaceImage(uploadValue);
            for (int k = 0; k < list.size(); k++) {
                list.get(k).put("imageUrl",
                        ResourceUtil.resourceBuildPath(req, list.get(k).get("resourceId").toString()));
            }
            pagerResult.setData(faceImageMapper.listFaceImage(uploadValue));
        }
        return pagerResult;
    }
}
