package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysBookMapper {
    int insert(SysBook book);
    SysBook selectById(Long id);
    int deleteByStoredFileId(Long storedFileId);
}
