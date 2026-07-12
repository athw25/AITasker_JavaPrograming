package com.aitasker.notification.email;

import com.aitasker.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Email Module", description = "Kiểm tra cấu hình gửi Email SMTP")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/test")
    @Operation(summary = "Gửi thử một email để kiểm tra cấu hình SMTP")
    public ApiResponse<Void> sendTestEmail(@jakarta.validation.Valid @RequestBody TestEmailRequest request) {
        emailService.send(request.getTo(), "AITasker — Email kiểm tra cấu hình",
                "Đây là email kiểm tra từ hệ thống AITasker. Nếu bạn nhận được email này, "
                        + "cấu hình SMTP đang hoạt động chính xác.");
        return ApiResponse.success("Đã gửi yêu cầu email kiểm tra (kiểm tra app.mail.enabled nếu không nhận được).", null);
    }

    @Data
    public static class TestEmailRequest {
        @NotBlank(message = "Địa chỉ email nhận không được để trống")
        private String to;
    }
}
