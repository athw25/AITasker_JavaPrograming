package com.aitasker.notification.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.notification.service.EmailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> testSendEmail(@Valid @RequestBody EmailTestRequest request) {
        emailService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
        return ApiResponse.success("Email test triggered successfully!");
    }

    @Getter
    @Setter
    public static class EmailTestRequest {
        @NotBlank(message = "Email to cannot be blank")
        @Email(message = "Must be a valid email format")
        private String to;

        @NotBlank(message = "Subject cannot be blank")
        private String subject;

        @NotBlank(message = "Body cannot be blank")
        private String body;
    }
}
