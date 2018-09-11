package com.base.web.dao.resource;
import com.base.web.bean.po.resource.Resources;
import com.base.web.dao.GenericMapper;

import java.util.List;
import java.util.Map;

/**
* 资源数据映射接口
* Created by generator 
*/
public interface ResourcesMapper extends GenericMapper<Resources,Long> {

    List<Resources> selectAllImage(Long recordId);

    int deleteByMd5(String md5);

    List<Map>  video (String videoName);

}
