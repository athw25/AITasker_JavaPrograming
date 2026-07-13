package com.aitasker.dispute.controller;

import com.aitasker.common.enums.DisputeStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.dispute.dto.response.DisputeResponse;
import com.aitasker.dispute.service.DisputeService;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.testsupport.WebMvcTestSecurityConfig;
import com.aitasker.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DisputeController.class)
@Import(WebMvcTestSecurityConfig.class)
class DisputeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DisputeService disputeService;

    private CustomUserDetails principal(Role role) {
        User user = new User();
        user.setId(1L);
        user.setEmail(role.name().toLowerCase() + "@example.com");
        user.setRole(role);
        return new CustomUserDetails(user);
    }

    @Test
    void addMessage_shouldReturn200_whenExpertSendsMessageOnOpenDispute() throws Exception {
        DisputeResponse response = DisputeResponse.builder()
                .id(500L)
                .status(DisputeStatus.OPEN)
                .build();

        when(disputeService.addMessage(eq(500L), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/disputes/500/messages")
                        .with(user(principal(Role.EXPERT)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("message", "Tôi sẽ bổ sung bằng chứng"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void addMessage_shouldReturn400_whenMessageBlank() throws Exception {
        mockMvc.perform(post("/api/disputes/500/messages")
                        .with(user(principal(Role.CLIENT)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("message", ""))))
                .andExpect(status().isBadRequest());

        verify(disputeService, never()).addMessage(any(), any(), any());
    }

    @Test
    void createDispute_shouldReturn201_whenClientCreatesDispute() throws Exception {
        DisputeResponse response = DisputeResponse.builder()
                .id(500L)
                .status(DisputeStatus.OPEN)
                .build();

        when(disputeService.createDispute(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/disputes")
                        .with(user(principal(Role.CLIENT)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                Map.of("projectId", 100, "reason", "Deliverable không đạt yêu cầu"))))
                .andExpect(status().isCreated());
    }

    @Test
    void createDispute_shouldReturn403_whenCallerIsAdmin() throws Exception {
        // Chỉ CLIENT/EXPERT (2 phía của Project) mới được tạo Dispute, ADMIN chỉ xử lý.
        mockMvc.perform(post("/api/disputes")
                        .with(user(principal(Role.ADMIN)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                Map.of("projectId", 100, "reason", "Deliverable không đạt yêu cầu"))))
                .andExpect(status().isForbidden());

        verify(disputeService, never()).createDispute(any(), any());
    }

    @Test
    void resolve_shouldReturn403_whenCallerIsNotAdmin() throws Exception {
        mockMvc.perform(put("/api/disputes/500/resolve")
                        .with(user(principal(Role.CLIENT)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                Map.of("status", "RESOLVED", "resolution", "Hoàn tiền 50%"))))
                .andExpect(status().isForbidden());

        verify(disputeService, never()).resolveDispute(any(), any(), any());
    }
}
