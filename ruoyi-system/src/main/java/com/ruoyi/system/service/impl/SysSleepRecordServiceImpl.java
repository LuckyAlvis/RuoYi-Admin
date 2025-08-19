package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.SysSleepRecord;
import com.ruoyi.system.mapper.SysSleepRecordMapper;
import com.ruoyi.system.service.SysSleepRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysSleepRecordServiceImpl implements SysSleepRecordService {

    @Resource
    private SysSleepRecordMapper mapper;

    @Override
    public List<SysSleepRecord> listByDateRange(Date start, Date end) {
        String username = SecurityUtils.getUsername();
        return mapper.selectByDateRange(start, end, username);
    }

    @Override
    public int insert(SysSleepRecord entity) {
        return mapper.insert(entity);
    }

    @Override
    public int update(SysSleepRecord entity) {
        return mapper.updateById(entity);
    }

    @Override
    public SysSleepRecord getByDate(Date date) {
        String username = SecurityUtils.getUsername();
        return mapper.selectByDateAndUser(date, username);
    }
}
