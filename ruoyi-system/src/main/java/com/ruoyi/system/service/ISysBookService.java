package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysBook;

public interface ISysBookService {
    int insert(SysBook book);
    int deleteByStoredFileId(Long storedFileId);
}
