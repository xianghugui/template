package com.base.web.service;

import com.base.web.bean.FaceImage;
import com.base.web.bean.UploadValue;
import com.base.web.bean.common.PagerResult;
import com.base.web.bean.common.QueryParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface FaceImageService extends GenericService<FaceImage, Long>{

    /**
     * 查询人脸图片
     * @param uploadValue
     * @return
     */
    PagerResult<Map> listFaceImage(UploadValue uploadValue, HttpServletRequest req);
}
