package com.aitasker.ai.openai;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Component
public class OpenAiClient {

    private final RestTemplate restTemplate;
    // Thay thế bằng API Key OpenAI thực tế của bạn hoặc nhóm
    private final String apiKey = "YOUR_OPENAI_API_KEY_HERE"; 
    private final String apiUrl = "https://api.openai.com/v1/chat/completions";

    public OpenAiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generate(String systemPrompt, String userPrompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // Cấu trúc Body Request chuẩn theo tài liệu API OpenAI
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo"); // Đổi .set thành .put

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userPrompt));
            
            requestBody.put("messages", messages);      // Đổi .set thành .put
            requestBody.put("temperature", 0.7);        // Đổi .set thành .put

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // 1. Chỉ định rõ List chứa các Map
                List<?> choices = (List<?>) response.getBody().get("choices");
                
                // 2. Chỉ định rõ Map nhận vào cặp dữ liệu String, Object
                Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                
                return (String) message.get("content");
            } else{
                throw new RuntimeException("Gọi API OpenAI thất bại, HTTP Status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            // Khi không có API Key thực tế hoặc lỗi mạng, trả về chuỗi JSON giả lập đúng cấu trúc để tránh crash hệ thống
            if (systemPrompt.contains("Job Assistant")) {
                return "{\"title\":\"Chuyên viên lập trình Java (Giả lập)\",\"description\":\"Phát triển hệ thống backend.\",\"skills\":[\"Java\",\"Spring Boot\"],\"budgetSuggestion\":\"15 - 20 triệu\"}";
            } else {
                return "{\"serviceDescription\":\"Dịch vụ AI tối ưu (Giả lập)\",\"tags\":[\"AI\",\"Java\"],\"pricingSuggestion\":\"5.000.000 VND\"}";
            }
        }
    }
}