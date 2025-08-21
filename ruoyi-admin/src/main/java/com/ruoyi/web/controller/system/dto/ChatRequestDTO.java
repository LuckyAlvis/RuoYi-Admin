package com.ruoyi.web.controller.system.dto;

import java.util.List;
import java.util.Map;

/**
 * 通用大模型聊天请求
 * 简洁摘要：承载通用聊天补全的请求参数
 */
public class ChatRequestDTO {
    /**
     * 模型名称，如 deepseek-chat
     */
    private String model;
    /**
     * 采样温度，默认 0.7
     */
    private Double temperature;
    /**
     * 系统提示词（system 角色）
     */
    private String systemPrompt;
    /**
     * 用户输入（优先级高于 summaryText）
     */
    private String userPrompt;
    /**
     * 业务侧传入的摘要内容（作为 userPrompt 的候选）
     */
    private String summaryText;
    /**
     * 自定义完整对话（如提供则优先使用），元素包含 role/content
     */
    private List<Map<String, String>> messages;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt) {
        this.userPrompt = userPrompt;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public List<Map<String, String>> getMessages() {
        return messages;
    }

    public void setMessages(List<Map<String, String>> messages) {
        this.messages = messages;
    }
}
