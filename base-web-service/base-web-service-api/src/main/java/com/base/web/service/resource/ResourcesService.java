package com.base.web.service.resource;


import com.base.web.bean.po.resource.Resources;
import com.base.web.service.GenericService;

import java.util.List;
import java.util.Map;

/**
 * 资源服务类
 * Created by generator
 */
public interface ResourcesService extends GenericService<Resources, Long> {
    /**
     * 根据资源md5 查询资源信息,如果没有资源则返回null
     *
     * @param md5 md5值
     * @return 资源对象
     */
    Resources selectByMd5(String md5);
    List<Long> selectImagesResourcesID(Long recordId);
    List<Resources> selectAllImage(Long recordId);

    List<Map>  video (String videoName);

}
