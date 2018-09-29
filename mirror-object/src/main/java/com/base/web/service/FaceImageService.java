package com.base.web.service;

import com.base.web.bean.FaceImage;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface FaceImageService extends GenericService<FaceImage, Long>{
    PagerResult<Map> queryAllFaceImage(QueryParam param, HttpServletRequest req);
    List<Map> queryAllFaceFeature(QueryParam param);
}
