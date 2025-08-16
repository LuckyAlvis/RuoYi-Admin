package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysStoredFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysStoredFileMapper {
    SysStoredFile selectById(@Param("id") Long id);

    SysStoredFile selectBySha256(@Param("sha256") String sha256);

    int insert(SysStoredFile file);

    int updateBizBinding(@Param("id") Long id, @Param("bizType") String bizType, @Param("bizId") Long bizId);
}
