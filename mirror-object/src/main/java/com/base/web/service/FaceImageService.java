package com.base.web.service;

import com.base.web.bean.FaceImage;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;

import java.util.List;
import java.util.Map;

public interface FaceImageService extends GenericService<FaceImage, Long>{
    PagerResult<Map> queryAllFaceImage(QueryParam param);
}
