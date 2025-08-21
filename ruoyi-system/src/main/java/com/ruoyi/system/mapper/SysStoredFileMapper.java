package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysStoredFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysStoredFileMapper {
    SysStoredFile selectById(@Param("id") Long id);

    SysStoredFile selectBySha256(@Param("sha256") String sha256);

    int insert(SysStoredFile file);

    int updateBizBinding(@Param("id") Long id, @Param("bizType") String bizType, @Param("bizId") Long bizId);

    List<SysStoredFile> selectList(@Param("bizType") String bizType,
                                   @Param("originalName") String originalName);

    List<SysStoredFile> selectList(@Param("bizType") String bizType,
                                   @Param("originalName") String originalName,
                                   @Param("mimeType") String mimeType);

    int deleteById(@Param("id") Long id);
}
