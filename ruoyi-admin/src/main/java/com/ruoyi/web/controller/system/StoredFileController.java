package com.ruoyi.web.controller.system;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.domain.SysStoredFile;
import com.ruoyi.system.service.ISysStoredFileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/system/storedFile")
public class StoredFileController extends BaseController {

    @Autowired
    private ISysStoredFileService sysStoredFileService;
    @Autowired
    private ServerConfig serverConfig;

//    @PreAuthorize("@ss.hasPermi('system:book:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "bizType", required = false) String bizType,
                              @RequestParam(value = "originalName", required = false) String originalName) {
        startPage();
        List<SysStoredFile> list = sysStoredFileService.selectList(bizType, originalName);
        return getDataTable(list);
    }

//    @PreAuthorize("@ss.hasPermi('system:book:upload')")
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "bizId", required = false) Long bizId) throws Exception {
        String filePath = RuoYiConfig.getUploadPath();
        String fileName = FileUploadUtils.upload(filePath, file);
        String url = serverConfig.getUrl() + fileName;
        Long userId = SecurityUtils.getUserId();
        SysStoredFile rec = sysStoredFileService.saveUploadedFile(file, fileName, url, userId);
        if (bizId != null) {
            sysStoredFileService.bindBiz(rec.getId(), "book_content", bizId);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("url", url);
        ajax.put("fileName", fileName);
        ajax.put("newFileName", FilenameUtils.getName(fileName));
        ajax.put("originalFilename", file.getOriginalFilename());
        ajax.put("fileId", rec.getId());
        return ajax;
    }
}
