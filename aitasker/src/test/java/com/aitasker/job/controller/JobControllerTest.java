package com.aitasker.job.controller;

import com.aitasker.common.enums.Role;
import com.aitasker.common.response.PageResponse;
import com.aitasker.job.dto.JobPostResponse;
import com.aitasker.job.service.JobService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobController.class)
@Import(WebMvcTestSecurityConfig.class)
class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobService jobService;

    private CustomUserDetails clientPrincipal() {
        User client = new User();
        client.setId(1L);
        client.setEmail("client@example.com");
        client.setRole(Role.CLIENT);
        return new CustomUserDetails(client);
    }

    @Test
    void getAll_shouldReturnPaginatedResponse() throws Exception {
        JobPostResponse job = new JobPostResponse();
        job.setId(1L);
        job.setTitle("Job 1");

        when(jobService.getAll(0, 10)).thenReturn(
                new PageResponse<>(List.of(job), 0, 10, 1, 1, true, true));

        mockMvc.perform(get("/api/jobs").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Job 1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAll_shouldDefaultToPageZeroSizeTen_whenNoParamsGiven() throws Exception {
        when(jobService.getAll(0, 10)).thenReturn(
                new PageResponse<>(List.of(), 0, 10, 0, 0, true, true));

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk());

        verify(jobService).getAll(0, 10);
    }

    @Test
    void create_shouldReturn403_whenCallerIsNotClient() throws Exception {
        User expert = new User();
        expert.setId(2L);
        expert.setEmail("expert@example.com");
        expert.setRole(Role.EXPERT);

        Map<String, Object> body = validJobBody();

        mockMvc.perform(post("/api/jobs")
                        .with(user(new CustomUserDetails(expert)))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());

        verify(jobService, never()).create(any());
    }

    @Test
    void create_shouldReturn400_whenBudgetIsNegative() throws Exception {
        Map<String, Object> body = validJobBody();
        body.put("budget", -100);

        mockMvc.perform(post("/api/jobs")
                        .with(user(clientPrincipal()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(jobService, never()).create(any());
    }

    @Test
    void create_shouldReturn400_whenDeadlineInPast() throws Exception {
        Map<String, Object> body = validJobBody();
        body.put("deadline", LocalDate.now().minusDays(1).toString());

        mockMvc.perform(post("/api/jobs")
                        .with(user(clientPrincipal()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(jobService, never()).create(any());
    }

    @Test
    void create_shouldReturn200_whenPayloadValidAndCallerIsClient() throws Exception {
        Map<String, Object> body = validJobBody();

        JobPostResponse response = new JobPostResponse();
        response.setId(10L);
        response.setTitle((String) body.get("title"));

        when(jobService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/jobs")
                        .with(user(clientPrincipal()))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(body.get("title")));
    }

    private Map<String, Object> validJobBody() {
        Map<String, Object> body = new HashMap<>();
        body.put("title", "Facebook Sales Chatbot");
        body.put("description", "Build an AI chatbot for Facebook Messenger");
        body.put("budget", 3000);
        body.put("deadline", LocalDate.now().plusDays(30).toString());
        body.put("requiredSkills", "Python, LangChain");
        return body;
    }
}
