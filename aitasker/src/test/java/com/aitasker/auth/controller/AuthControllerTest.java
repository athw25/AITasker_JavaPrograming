package com.aitasker.auth.controller;

import com.aitasker.auth.service.AuthService;
import com.aitasker.testsupport.WebMvcTestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(WebMvcTestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void forgotPassword_shouldReturn200_whenEmailValid() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("email", "client@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(authService).forgotPassword(any());
    }

    @Test
    void forgotPassword_shouldReturn400_whenEmailBlank() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("email", ""))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void forgotPassword_shouldReturn400_whenEmailInvalidFormat() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("email", "not-an-email"))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void resetPassword_shouldReturn200_whenPayloadValid() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                Map.of("token", "abc-123", "newPassword", "newSecret1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(authService).resetPassword(any());
    }

    @Test
    void resetPassword_shouldReturn400_whenNewPasswordTooShort() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                Map.of("token", "abc-123", "newPassword", "123"))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    void resetPassword_shouldReturn400_whenTokenBlank() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                Map.of("token", "", "newPassword", "newSecret1"))))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }
}
