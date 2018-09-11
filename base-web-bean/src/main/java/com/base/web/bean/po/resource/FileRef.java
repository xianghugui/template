package com.base.web.bean.po.resource;

import com.base.web.bean.po.GenericPo;

public class FileRef extends GenericPo<Long> {

    //资源ID
    private Long resourceId;
    //资源关联id
    private Long refId;
    //类型： 0 图片，1 视频
    private Integer type;
    //广告资源优先级（数字越小优先级越高，默认没有优先级0）
    private Integer priority;
    //1营业执照id，2，商品详情图片id，3商品轮播id,4：店铺LOGO，5：店铺图片，6商品品论图片,7拒绝退款图片,8申请退款图片
    private Integer dataType;

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public interface Property extends GenericPo.Property{
        //区域名称
        String resourceId="resourceId";
        //
        String refId="refId";
        //
        String type="type";

        String priority="priority";

        String dataType="dataType";
    }
}
