package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 定时剪辑视频配置表 video_clip_config
 */
public class VideoClipConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Boolean enabled;

    private String materialList; // JSON array or comma-separated list

    private String productName;

    private String productFeature;

    private Integer clipCount;

    private Integer videoLengthMin;

    private Integer videoLengthMax;

    private String clipTime; // e.g. "13:20"

    private String aspectRatio; // e.g. 9:16

    private String resolution; // e.g. 1440x2560

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getMaterialList() {
        return materialList;
    }

    public void setMaterialList(String materialList) {
        this.materialList = materialList;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductFeature() {
        return productFeature;
    }

    public void setProductFeature(String productFeature) {
        this.productFeature = productFeature;
    }

    public Integer getClipCount() {
        return clipCount;
    }

    public void setClipCount(Integer clipCount) {
        this.clipCount = clipCount;
    }

    public Integer getVideoLengthMin() {
        return videoLengthMin;
    }

    public void setVideoLengthMin(Integer videoLengthMin) {
        this.videoLengthMin = videoLengthMin;
    }

    public Integer getVideoLengthMax() {
        return videoLengthMax;
    }

    public void setVideoLengthMax(Integer videoLengthMax) {
        this.videoLengthMax = videoLengthMax;
    }

    public String getClipTime() {
        return clipTime;
    }

    public void setClipTime(String clipTime) {
        this.clipTime = clipTime;
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("enabled", getEnabled())
            .append("materialList", getMaterialList())
            .append("productName", getProductName())
            .append("productFeature", getProductFeature())
            .append("clipCount", getClipCount())
            .append("videoLengthMin", getVideoLengthMin())
            .append("videoLengthMax", getVideoLengthMax())
            .append("clipTime", getClipTime())
            .append("aspectRatio", getAspectRatio())
            .append("resolution", getResolution())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
