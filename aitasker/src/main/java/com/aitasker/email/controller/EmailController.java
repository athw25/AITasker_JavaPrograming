package com.aitasker.email.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.email.dto.TestEmailRequest;
import com.aitasker.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> test(@Valid @RequestBody TestEmailRequest request) {
        emailService.send(request.getTo(), request.getSubject(), request.getBody());
        return ApiResponse.success("Đã gửi (hoặc bỏ qua nếu mail chưa cấu hình)", null);
    }
}
