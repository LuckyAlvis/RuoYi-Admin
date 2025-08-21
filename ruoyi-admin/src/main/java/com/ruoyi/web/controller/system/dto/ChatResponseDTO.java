package com.ruoyi.web.controller.system.dto;

/**
 * 通用大模型聊天响应
 * 简洁摘要：承载通用聊天补全的主要返回文本
 */
public class ChatResponseDTO {
    /**
     * 聊天回复的主要文本内容
     */
    private String content;

    public ChatResponseDTO() {
    }

    public ChatResponseDTO(String content) {
        this.content = content;
    }

    /**
     * 获取主要文本内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置主要文本内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}
