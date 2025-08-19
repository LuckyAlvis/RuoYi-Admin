package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysSleepRecord;

import java.util.Date;
import java.util.List;

public interface SysSleepRecordService {
    List<SysSleepRecord> listByDateRange(Date start, Date end);

    int insert(SysSleepRecord entity);

    int update(SysSleepRecord entity);

    SysSleepRecord getByDate(Date date);
}
