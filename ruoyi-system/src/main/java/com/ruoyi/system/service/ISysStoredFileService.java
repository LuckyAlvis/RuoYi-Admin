package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysStoredFile;
import org.springframework.web.multipart.MultipartFile;

public interface ISysStoredFileService {
    SysStoredFile getById(Long id);

    SysStoredFile getBySha256(String sha256);

    SysStoredFile saveUploadedFile(MultipartFile file, String storagePath, String storageUrl, Long uploaderUserId);

    int bindBiz(Long fileId, String bizType, Long bizId);
}
