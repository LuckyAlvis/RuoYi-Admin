package com.ruoyi.web.controller.common;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.config.ServerConfig;
import com.ruoyi.system.domain.SysStoredFile;
import com.ruoyi.system.service.ISysStoredFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用请求处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/common")
public class CommonController {
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);
    private static final String FILE_DELIMETER = ",";
    @Autowired
    private ServerConfig serverConfig;
    @Autowired
    private ISysStoredFileService sysStoredFileService;

    /**
     * 通用下载请求
     *
     * @param fileName 文件名称
     * @param delete   是否删除
     */
    @GetMapping("/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request) {
        try {
            if (!FileUtils.checkAllowDownload(fileName)) {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String realFileName = System.currentTimeMillis() + fileName.substring(fileName.indexOf("_") + 1);
            String filePath = RuoYiConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete) {
                FileUtils.deleteFile(filePath);
            }
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求（单个）
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) throws Exception {
        try {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            // 上传并返回新文件名称
            String fileName = FileUploadUtils.upload(filePath, file);
            String url = serverConfig.getUrl() + fileName;
            // 记录到 sys_file
            Long userId = SecurityUtils.getUserId();
            SysStoredFile rec = sysStoredFileService.saveUploadedFile(
                    file,
                    fileName, // storage_path 使用资源前缀路径
                    url,      // storage_url 为可访问URL
                    userId
            );
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", fileName);
            ajax.put("newFileName", FileUtils.getName(fileName));
            ajax.put("originalFilename", file.getOriginalFilename());
            ajax.put("fileId", rec.getId());
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通用上传请求（多个）
     */
    @PostMapping("/uploads")
    public AjaxResult uploadFiles(List<MultipartFile> files) throws Exception {
        try {
            // 上传文件路径
            String filePath = RuoYiConfig.getUploadPath();
            List<String> urls = new ArrayList<String>();
            List<String> fileNames = new ArrayList<String>();
            List<String> newFileNames = new ArrayList<String>();
            List<String> originalFilenames = new ArrayList<String>();
            List<String> fileIds = new ArrayList<String>();
            Long userId = SecurityUtils.getUserId();
            for (MultipartFile file : files) {
                // 上传并返回新文件名称
                String fileName = FileUploadUtils.upload(filePath, file);
                String url = serverConfig.getUrl() + fileName;
                SysStoredFile rec = sysStoredFileService.saveUploadedFile(file, fileName, url, userId);
                urls.add(url);
                fileNames.add(fileName);
                newFileNames.add(FileUtils.getName(fileName));
                originalFilenames.add(file.getOriginalFilename());
                fileIds.add(String.valueOf(rec.getId()));
            }
            AjaxResult ajax = AjaxResult.success();
            ajax.put("urls", StringUtils.join(urls, FILE_DELIMETER));
            ajax.put("fileNames", StringUtils.join(fileNames, FILE_DELIMETER));
            ajax.put("newFileNames", StringUtils.join(newFileNames, FILE_DELIMETER));
            ajax.put("originalFilenames", StringUtils.join(originalFilenames, FILE_DELIMETER));
            ajax.put("fileIds", StringUtils.join(fileIds, FILE_DELIMETER));
            return ajax;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            if (!FileUtils.checkAllowDownload(resource)) {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = RuoYiConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 文件流下载
     *
     * @param id
     * @param request
     * @param response
     */
    @GetMapping(value = "/file/stream{id}")
    public void stream(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        SysStoredFile rec = sysStoredFileService.getById(id);
        if (rec == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        String storagePath = rec.getStoragePath();
        try {
            String localRoot = RuoYiConfig.getProfile();
            String relative = StringUtils.substringAfter(storagePath, Constants.RESOURCE_PREFIX);
            String filePath = localRoot + relative;
            File file = new File(filePath);
            if (!file.exists()) {
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return;
            }

            String mime = rec.getMimeType();
            if (StringUtils.isEmpty(mime)) {
                mime = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
            response.setHeader("Accept-Ranges", "bytes");
            response.setContentType(mime);

            String range = request.getHeader("Range");
            long fileLength = file.length();
            long start = 0;
            long end = fileLength - 1;
            int status = HttpStatus.OK.value();

            if (StringUtils.isNotEmpty(range) && range.startsWith("bytes=")) {
                status = HttpStatus.PARTIAL_CONTENT.value();
                String[] parts = range.substring(6).split("-");
                try {
                    if (parts.length > 0 && StringUtils.isNotEmpty(parts[0])) {
                        start = Long.parseLong(parts[0]);
                    }
                    if (parts.length > 1 && StringUtils.isNotEmpty(parts[1])) {
                        end = Long.parseLong(parts[1]);
                    }
                } catch (NumberFormatException ignore) {
                    start = 0;
                    end = fileLength - 1;
                    status = HttpStatus.OK.value();
                }
                if (end >= fileLength) {
                    end = fileLength - 1;
                }
                if (start > end || start < 0) {
                    start = 0;
                    end = fileLength - 1;
                    status = HttpStatus.OK.value();
                }
                long contentLength = end - start + 1;
                response.setStatus(status);
                response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);
                response.setHeader("Content-Length", String.valueOf(contentLength));
                try (RandomAccessFile raf = new RandomAccessFile(file, "r");) {
                    raf.seek(start);
                    byte[] buffer = new byte[8192];
                    long remaining = contentLength;
                    while (remaining > 0) {
                        int toRead = (int) Math.min(buffer.length, remaining);
                        int read = raf.read(buffer, 0, toRead);
                        if (read == -1) break;
                        response.getOutputStream().write(buffer, 0, read);
                        remaining -= read;
                    }
                }
            } else {
                response.setHeader("Content-Length", String.valueOf(fileLength));
                // 复用现有工具直接写全量
                FileUtils.writeBytes(filePath, response.getOutputStream());
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
