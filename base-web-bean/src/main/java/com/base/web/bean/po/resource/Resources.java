package com.base.web.bean.po.resource;

import com.base.web.bean.po.GenericPo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hsweb.commons.file.FileUtils;

import java.util.Date;


/**
 * 资源类实体
 */
@ApiModel(value = "Resources", description = "资源对象")
public class Resources extends GenericPo<Long> {

    //资源文件名
    @ApiModelProperty(value = "资源名称")
    private String name;
    //资源文件名
    @ApiModelProperty(value = "资源存放路径")
    private String path;
    //资源类型
    @ApiModelProperty(value = "资源类型")
    private String type;
    //md5码
    @ApiModelProperty(value = "资源名MD5")
    private String md5;
    //资源文件名
    @ApiModelProperty(value = "资源大小")
    private long size;
    //资源创建者id
    @ApiModelProperty(value = "资源添加人")
    private Long createId;
    //创建时间
    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSuffix() {
        return FileUtils.getSuffix(getName());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Long getCreateId() {
        return createId;
    }

    public void setCreateId(Long createId) {
        this.createId = createId;
    }

    public interface Property extends GenericPo.Property{
        //区域名称
        String name="name";
        //
        String path="path";
        //
        String type="type";

        String md5="md5";

        String size="size";

        String createId="createId";
        String createTime="createTime";

    }
}
