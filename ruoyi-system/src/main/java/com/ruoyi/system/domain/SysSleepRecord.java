package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 睡眠记录实体
 */
public class SysSleepRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 记录日期
     */
    private Date recordDate;
    /**
     * 入睡时间 HH:mm
     */
    private String sleepTime;
    /**
     * 起床时间 HH:mm
     */
    private String wakeTime;
    /**
     * 总睡眠分钟（可空）
     */
    private Integer totalMinutes;
    /**
     * 删除标志（0存在 2删除）
     */
    private String delFlag;
    /**
     * 备注
     */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getWakeTime() {
        return wakeTime;
    }

    public void setWakeTime(String wakeTime) {
        this.wakeTime = wakeTime;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
