package com.base.web.dao;

import com.base.web.bean.FaceFeature;
import com.base.web.bean.FaceImage;
import com.base.web.bean.UploadValue;
import com.base.web.bean.common.QueryParam;

import java.util.List;
import java.util.Map;

public interface FaceImageMapper extends GenericMapper<FaceImage, Long>{

    /**
     * 根据摄像头ID查询全部监测图片
     * @param param
     * @return
     */
    List<Map> queryAllFaceImage(QueryParam param);

    int queryFaceImageTotal(QueryParam param);

    List<Map> queryAllFaceFeature(UploadValue uploadValue);
    List<Map> listFaceImage(UploadValue uploadValue);
    int listFaceImageTotal(UploadValue uploadValue);
}
