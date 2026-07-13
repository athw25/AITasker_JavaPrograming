package com.aitasker.admin.controller;

import com.aitasker.admin.service.AdminUserService;
import com.aitasker.common.enums.Role;
import com.aitasker.common.response.PageResponse;
import com.aitasker.security.userdetails.CustomUserDetails;
import com.aitasker.testsupport.WebMvcTestSecurityConfig;
import com.aitasker.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminUserController.class)
@Import(WebMvcTestSecurityConfig.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminUserService adminUserService;

    private CustomUserDetails principal(Role role) {
        User u = new User();
        u.setId(1L);
        u.setEmail(role.name().toLowerCase() + "@example.com");
        u.setRole(role);
        return new CustomUserDetails(u);
    }

    @Test
    void getAllUsers_shouldReturn403_whenCallerIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users").with(user(principal(Role.CLIENT))))
                .andExpect(status().isForbidden());

        verifyNoInteractions(adminUserService);
    }

    @Test
    void getAllUsers_shouldPassKeywordRoleAndPaging_toService() throws Exception {
        when(adminUserService.getAllUsers("john", Role.EXPERT, 1, 5))
                .thenReturn(new PageResponse<>(List.of(), 1, 5, 0, 0, false, true));

        mockMvc.perform(get("/api/admin/users")
                        .with(user(principal(Role.ADMIN)))
                        .param("keyword", "john")
                        .param("role", "EXPERT")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(adminUserService).getAllUsers("john", Role.EXPERT, 1, 5);
    }

    @Test
    void getAllUsers_shouldDefaultToPageZeroSizeTen_whenNoParams() throws Exception {
        when(adminUserService.getAllUsers(null, null, 0, 10))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0, true, true));

        mockMvc.perform(get("/api/admin/users").with(user(principal(Role.ADMIN))))
                .andExpect(status().isOk());

        verify(adminUserService).getAllUsers(null, null, 0, 10);
    }
}
