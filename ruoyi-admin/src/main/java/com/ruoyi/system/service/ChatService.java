package com.ruoyi.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用聊天服务
 * 简洁摘要 + @param/@return
 */
@Service
public class ChatService {

    @Value("${deepseek.apiKey:sk-d47bd533808f41d4865b1f2ac92b0a93}")
    private String deepseekApiKey;

    /**
     * 调用 DeepSeek Chat Completions
     *
     * @param messages    对话消息列表，元素包含 role/content
     * @param model       模型名，默认 deepseek-chat
     * @param temperature 温度，默认 0.7
     * @return 第一个回复的 content 文本
     */
    @SuppressWarnings("unchecked")
    public String complete(List<Map<String, String>> messages, String model, Double temperature) {
        String apiKey = resolveApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException("DeepSeek API Key 未配置");
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("model", (model == null || model.isEmpty()) ? "deepseek-chat" : model);
        if (temperature != null) payload.put("temperature", temperature);
        payload.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(10_000);
            factory.setReadTimeout(120_000);
            restTemplate.setRequestFactory(factory);
        } catch (Exception ignore) {
        }

        ResponseEntity<String> resp = restTemplate.postForEntity("https://api.deepseek.com/chat/completions", httpEntity, String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("DeepSeek API 调用失败：" + resp.getStatusCodeValue() + " - " + resp.getBody());
        }
        return parseFirstContent(resp.getBody());
    }

    private String resolveApiKey() {
        if (deepseekApiKey != null && !deepseekApiKey.isEmpty()) return deepseekApiKey;
        String env = System.getenv("DEEPSEEK_API_KEY");
        return env == null ? "" : env;
    }

    // 提取 choices[0].message.content
    @SuppressWarnings("unchecked")
    private String parseFirstContent(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> root = mapper.readValue(json.getBytes(StandardCharsets.UTF_8), Map.class);
            List<Object> choices = (List<Object>) root.get("choices");
            if (choices == null || choices.isEmpty()) return json;
            Object first = choices.get(0);
            if (!(first instanceof Map)) return json;
            Map<String, Object> m = (Map<String, Object>) first;
            Map<String, Object> message = (Map<String, Object>) m.get("message");
            if (message == null) return json;
            Object content = message.get("content");
            return content == null ? json : content.toString();
        } catch (Exception ex) {
            return json;
        }
    }

    public static Map<String, String> msg(String role, String content) {
        Map<String, String> m = new HashMap<>();
        m.put("role", role);
        m.put("content", content);
        return m;
    }
}
