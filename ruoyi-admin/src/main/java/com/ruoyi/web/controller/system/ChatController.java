package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.ChatService;
import com.ruoyi.web.controller.system.dto.ChatRequestDTO;
import com.ruoyi.web.controller.system.dto.ChatResponseDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 通用大模型聊天控制器
 * 简洁摘要 + @param/@return
 */
@RestController
@RequestMapping("/system/chat")
public class ChatController extends BaseController {

    @Resource
    private ChatService chatService;

    /**
     * 通用对话补全接口
     *
     * @param req ChatRequestDTO，包含模型名/温度/系统提示/用户提示/自定义消息等
     * @return ChatResponseDTO 包含 content 文本
     */
    @PreAuthorize("@ss.hasPermi('system:chat:complete') or true")
    @PostMapping("/complete")
    public AjaxResult complete(@RequestBody ChatRequestDTO req) {
        try {
            List<Map<String, String>> messages = req.getMessages();
            if (messages == null || messages.isEmpty()) {
                messages = new ArrayList<>();
                String sys = (req.getSystemPrompt() == null || req.getSystemPrompt().isEmpty()) ? DEFAULT_SYSTEM_PROMPT : req.getSystemPrompt();
                String user = (req.getUserPrompt() != null && !req.getUserPrompt().isEmpty()) ? req.getUserPrompt() : (req.getSummaryText() == null ? "" : req.getSummaryText());
                messages.add(ChatService.msg("system", sys));
                messages.add(ChatService.msg("user", user));
            }
            String content = chatService.complete(messages, req.getModel(), req.getTemperature());
            return AjaxResult.success(new ChatResponseDTO(content));
        } catch (Exception e) {
            return AjaxResult.error("Chat 调用失败：" + e.getMessage());
        }
    }

    // 默认系统提示词（通用）
    private static final String DEFAULT_SYSTEM_PROMPT = "你是一名专业助手，请基于用户的输入给出清晰、客观、可执行的回复。";
}
