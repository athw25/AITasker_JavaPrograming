package com.aitasker.expert.controller;

import com.aitasker.common.enums.Role;
import com.aitasker.expert.entity.Attachment;
import com.aitasker.expert.service.FileStorageService;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.testsupport.WebMvcTestSecurityConfig;
import com.aitasker.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileStorageController.class)
@Import(WebMvcTestSecurityConfig.class)
class FileStorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    private CustomUserDetails principal(Role role, Long id) {
        User u = new User();
        u.setId(id);
        u.setEmail(role.name().toLowerCase() + "@example.com");
        u.setRole(role);
        return new CustomUserDetails(u);
    }

    @Test
    void upload_shouldSucceed_forClient() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "delivery.pdf", "application/pdf", "content".getBytes());
        when(fileStorageService.uploadFile(any(), eq(1L), eq(100L))).thenReturn(new Attachment());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("projectId", "100")
                        .with(user(principal(Role.CLIENT, 1L))))
                .andExpect(status().isOk());

        verify(fileStorageService).uploadFile(any(), eq(1L), eq(100L));
    }

    @Test
    void upload_shouldSucceed_forExpert_withoutProjectId() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "portfolio.png", "image/png", "content".getBytes());
        when(fileStorageService.uploadFile(any(), eq(2L), isNull())).thenReturn(new Attachment());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .with(user(principal(Role.EXPERT, 2L))))
                .andExpect(status().isOk());

        verify(fileStorageService).uploadFile(any(), eq(2L), isNull());
    }

    @Test
    void upload_shouldReturn403_forAdmin() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "x.png", "image/png", "content".getBytes());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .with(user(principal(Role.ADMIN, 3L))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(fileStorageService);
    }
}
