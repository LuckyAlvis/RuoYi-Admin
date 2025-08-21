package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.SysStoredFile;
import com.ruoyi.system.mapper.SysStoredFileMapper;
import com.ruoyi.system.service.ISysStoredFileService;
import com.ruoyi.system.service.ISysBookService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class SysStoredFileServiceImpl implements ISysStoredFileService {

    @Autowired
    private SysStoredFileMapper sysStoredFileMapper;
    @Autowired
    private ISysBookService sysBookService;

    @Override
    public SysStoredFile getById(Long id) {
        return sysStoredFileMapper.selectById(id);
    }

    @Override
    public SysStoredFile getBySha256(String sha256) {
        return sysStoredFileMapper.selectBySha256(sha256);
    }

    @Override
    public SysStoredFile saveUploadedFile(MultipartFile file, String storagePath, String storageUrl, Long uploaderUserId) {
        String sha256 = FileUtils.sha256Hex(file);
//        SysStoredFile existed = sysStoredFileMapper.selectBySha256(sha256);
//        if (existed != null) {
//            return existed;
//        }
        SysStoredFile rec = new SysStoredFile();
        rec.setBizType(null);
        rec.setBizId(null);
        rec.setOriginalName(file.getOriginalFilename());
        rec.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
        rec.setMimeType(file.getContentType());
        rec.setStoragePath(storagePath);
        rec.setStorageUrl(storageUrl);
        rec.setFileSize(file.getSize());
        rec.setSha256(sha256);
        rec.setUploaderUserId(uploaderUserId);
        sysStoredFileMapper.insert(rec);
        return rec;
    }

    @Override
    public int bindBiz(Long fileId, String bizType, Long bizId) {
        return sysStoredFileMapper.updateBizBinding(fileId, bizType, bizId);
    }

    @Override
    public List<SysStoredFile> selectList(String bizType, String originalName, String mimeType) {
        return sysStoredFileMapper.selectList(bizType, originalName, mimeType);
    }

    @Override
    public int deleteById(Long id) {
        // 先删除子表（书籍）记录，避免外键约束错误
        sysBookService.deleteByStoredFileId(id);
        return sysStoredFileMapper.deleteById(id);
    }
}
