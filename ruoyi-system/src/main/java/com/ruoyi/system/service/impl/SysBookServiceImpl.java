package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysBook;
import com.ruoyi.system.mapper.SysBookMapper;
import com.ruoyi.system.service.ISysBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysBookServiceImpl implements ISysBookService {

    @Autowired
    private SysBookMapper sysBookMapper;

    @Override
    public int insert(SysBook book) {
        return sysBookMapper.insert(book);
    }

    @Override
    public int deleteByStoredFileId(Long storedFileId) {
        return sysBookMapper.deleteByStoredFileId(storedFileId);
    }
}
