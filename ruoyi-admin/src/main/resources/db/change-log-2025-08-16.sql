-- 变更记录 | 2025-08-16 20:35 | 作者：yifan.dai
-- 目的：为读书进度管理 MVP 引入通用文件存储表；保留书籍表以支撑元数据管理。
-- 原因：
--  1) 采用通用文件表 sys_file 统一落表，避免为单一业务（书籍）重复设计文件表。
--  2) 移除外键，改由应用层保证一致性，提升灵活性；避免跨模块耦合。
--  3) 避免超长唯一索引报错（MySQL 1071）：对 storage_url/path 仅建前缀索引，唯一性用 sha256 保证。

-- A) 书籍表（不区分版本）
CREATE TABLE IF NOT EXISTS `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(256) NOT NULL COMMENT '书名',
  `subtitle` VARCHAR(256) NULL COMMENT '副标题',
  `author` VARCHAR(256) NULL COMMENT '作者',
  `language` VARCHAR(32) NULL COMMENT '语言（如 zh-CN, en）',
  `cover_url` VARCHAR(512) NULL COMMENT '封面图片URL/路径',
  `description` TEXT NULL COMMENT '简介/描述',
  `total_pages` INT NULL COMMENT '全书总页数（可选）',
  `total_words` BIGINT NULL COMMENT '全书总字数（可选）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_book_title` (`title`),
  KEY `idx_book_author` (`author`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍元数据';

-- 变更记录 | 2025-08-16 21:24 | 作者：yifan.dai
-- Notes:
-- - 引入通用文件表 `sys_stored_file`（无外键），以 sha256 作为全局唯一约束。
-- - 兼容现有 RuoYi /common/upload 接口，作为文件元数据的统一落表。
-- - 后续业务通过 biz_type + biz_id 关联（应用层保证一致性）。
-- - 不对现有表做破坏性变更；MVP 阶段可不再使用 `book_file` 表。
CREATE TABLE IF NOT EXISTS `sys_stored_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `biz_type` VARCHAR(64) NULL COMMENT '业务类型，如 book_content / book_cover / avatar 等',
    `biz_id` BIGINT NULL COMMENT '业务ID（如书籍ID）',
    `original_name` VARCHAR(512) NULL COMMENT '原始文件名',
    `extension` VARCHAR(32) NULL COMMENT '扩展名（小写）',
    `mime_type` VARCHAR(128) NULL COMMENT 'MIME 类型',
    `storage_path` VARCHAR(1024) NOT NULL COMMENT '存储相对路径（如 /upload/2025/08/16/xxx.pdf）',
    `storage_url` VARCHAR(1024) NULL COMMENT '对外访问URL（可选，视部署而定）',
    `file_size` BIGINT NULL COMMENT '大小（字节）',
    `sha256` CHAR(64) NOT NULL COMMENT '文件 SHA-256（全局唯一约束）',
    `uploader_user_id` BIGINT NULL COMMENT '上传者用户ID（可选，应用层约束）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_stored_file_sha256` (`sha256`),
    KEY `idx_sys_stored_file_biz` (`biz_type`, `biz_id`),
    KEY `idx_sys_stored_file_url_prefix` (`storage_url`(255)),
    KEY `idx_sys_stored_file_path_prefix` (`storage_path`(255))
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通用文件存储表';
