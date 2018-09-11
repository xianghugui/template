package com.base.web.service.impl.resource;

import com.base.web.bean.common.QueryParam;
import com.base.web.bean.po.resource.Resources;
import com.base.web.dao.resource.ResourcesMapper;
import com.base.web.service.impl.AbstractServiceImpl;
import com.base.web.service.resource.ResourcesService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 资源服务类
 * Created by generator
 */
@Service("ResourcesService")
public class ResourcesServiceImpl extends AbstractServiceImpl<Resources, Long> implements ResourcesService {
    public static final String CACHE_KEY = "resources";

    //默认数据映射接口
    @Resource
    protected ResourcesMapper resourcesMapper;

    //默认数据映射接口
    @Override
    protected ResourcesMapper getMapper() {
        return this.resourcesMapper;
    }



    public int deleteByMd5(String md5) {
        return getMapper().deleteByMd5(md5);
    }

    /**
     * 根据资源md5 查询资源信息
     *
     * @param md5 md5值
     * @return 资源对象
     * @throws Exception
     */
    @Cacheable(value = CACHE_KEY, key = "'md5.'+#md5")
    @Transactional(readOnly = true)
    public Resources selectByMd5(String md5) {
        return this.selectSingle(new QueryParam().where("md5", md5));
    }

    @Override
    public List<Resources> selectAllImage(Long recordId) {
        return resourcesMapper.selectAllImage(recordId);
    }

    public List<Long> selectImagesResourcesID(Long recordId) {
        List<Long> imageUrls = new ArrayList<>();
        List<Resources> images = this.selectAllImage(recordId);
        for (Resources image : images) {
            if (image != null && image.getId() != null) {
                imageUrls.add(image.getId());
            }
        }
        return imageUrls;
    }

    @Override
    public List<Map> video(String videoName) {
        return getMapper().video(videoName);
    }



}
