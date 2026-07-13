package com.aitasker.ai.controller;

import com.aitasker.ai.assistant.JobAssistantService;
import com.aitasker.ai.assistant.ServiceGeneratorService;
import com.aitasker.ai.dto.response.JobAssistantResponse;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.Role;
import com.aitasker.exception.AiUnavailableException;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.testsupport.WebMvcTestSecurityConfig;
import com.aitasker.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiAssistantController.class)
@Import(WebMvcTestSecurityConfig.class)
class AiAssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobAssistantService jobAssistantService;
    @MockBean
    private ServiceGeneratorService serviceGeneratorService;
    @MockBean
    private AnalyticsService analyticsService;

    private CustomUserDetails clientPrincipal() {
        User u = new User();
        u.setId(1L);
        u.setEmail("client@example.com");
        u.setRole(Role.CLIENT);
        return new CustomUserDetails(u);
    }

    @Test
    void assistJob_shouldReturn400_whenPromptBlank() throws Exception {
        mockMvc.perform(post("/api/ai/job-assistant")
                        .param("prompt", "")
                        .with(user(clientPrincipal())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(jobAssistantService);
    }

    @Test
    void assistJob_shouldReturn200_andRecordAnalyticsEvent_whenPromptValid() throws Exception {
        JobAssistantResponse response = JobAssistantResponse.builder()
                .title("Facebook Sales Chatbot")
                .description("desc")
                .estimatedBudget(new BigDecimal("3000"))
                .currency("USD")
                .build();
        when(jobAssistantService.generateJobDescription("Tôi muốn chatbot bán hàng Facebook"))
                .thenReturn(response);

        mockMvc.perform(post("/api/ai/job-assistant")
                        .param("prompt", "Tôi muốn chatbot bán hàng Facebook")
                        .with(user(clientPrincipal())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Facebook Sales Chatbot"));

        verify(analyticsService).recordEvent(any(), eq(1L), eq("CLIENT"), any(), any());
    }

    @Test
    void assistJob_shouldReturn503_whenAiProviderDisabled() throws Exception {
        when(jobAssistantService.generateJobDescription(any()))
                .thenThrow(new AiUnavailableException("AI Job Assistant hiện không khả dụng."));

        mockMvc.perform(post("/api/ai/job-assistant")
                        .param("prompt", "Tôi muốn chatbot bán hàng Facebook")
                        .with(user(clientPrincipal())))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void generateService_shouldReturn400_whenPromptBlank() throws Exception {
        mockMvc.perform(post("/api/ai/service-generator")
                        .param("prompt", "")
                        .with(user(clientPrincipal())))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(serviceGeneratorService);
    }
}
