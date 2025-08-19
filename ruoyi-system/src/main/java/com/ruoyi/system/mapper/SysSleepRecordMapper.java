package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysSleepRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 睡眠记录 Mapper
 */
public interface SysSleepRecordMapper {
    List<SysSleepRecord> selectByDateRange(@Param("start") Date start, @Param("end") Date end, @Param("username") String username);

    int insert(SysSleepRecord entity);

    int updateById(SysSleepRecord entity);

    SysSleepRecord selectByDateAndUser(@Param("date") Date date, @Param("username") String username);
}
