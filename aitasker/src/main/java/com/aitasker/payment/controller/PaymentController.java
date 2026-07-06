package com.aitasker.payment.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.payment.dto.*;
import com.aitasker.payment.entity.*;
import com.aitasker.payment.service.PaymentService;
import com.aitasker.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PaymentController — dùng SecurityContext pattern của Nguyễn Quốc Đạt.
 *
 * Cách lấy userId từ JWT:
 *   @AuthenticationPrincipal CustomUserDetails userDetails
 *   → userDetails.getUser().getId()
 *
 * CustomUserDetails được JwtFilter nạp vào SecurityContext sau khi xác thực token.
 * KHÔNG tự parse JWT thủ công trong Controller.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "API quản lý thanh toán Escrow")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Client nạp tiền vào Escrow cho một Project hoặc Milestone.
     * clientId lấy từ JWT qua SecurityContext — theo chuẩn Đạt (Bảo mật).
     */
    @PostMapping("/deposit")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Nạp tiền vào Escrow", description = "Client nạp tiền giữ cho project/milestone")
    public ResponseEntity<ApiResponse<Payment>> deposit(
            @Valid @RequestBody DepositRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long clientId = userDetails.getUser().getId();
        Payment payment = paymentService.deposit(request, clientId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    /**
     * Client giải ngân cho Expert sau khi Milestone được APPROVED.
     * clientId lấy từ JWT qua SecurityContext — theo chuẩn Đạt (Bảo mật).
     */
    @PutMapping("/release")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Release tiền cho Expert", description = "Client giải ngân sau khi Milestone APPROVED")
    public ResponseEntity<ApiResponse<Payment>> release(
            @Valid @RequestBody ReleaseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long clientId = userDetails.getUser().getId();
        Payment payment = paymentService.release(request, clientId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    /**
     * Xem lịch sử giao dịch theo paymentId.
     * Không cần clientId — ai có quyền đều xem được.
     */
    @GetMapping("/transactions/{paymentId}")
    @Operation(summary = "Xem lịch sử giao dịch theo Payment")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactions(
            @PathVariable Long paymentId
    ) {
        List<Transaction> transactions = paymentService.getTransactionHistory(paymentId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/transactions/me")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Xem lịch sử giao dịch của Expert đang đăng nhập")
    public ResponseEntity<ApiResponse<List<Transaction>>> getMyTransactions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long expertId = userDetails.getUser().getId();
        List<Transaction> transactions = paymentService.getTransactionHistoryForExpert(expertId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @PutMapping("/{paymentId}/release")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Release tiền cho Expert theo paymentId", description = "Client giải ngân sau khi Milestone APPROVED")
    public ResponseEntity<ApiResponse<Payment>> releaseByPaymentId(
            @PathVariable Long paymentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ReleaseRequest request = new ReleaseRequest();
        request.setPaymentId(paymentId);
        Long clientId = userDetails.getUser().getId();
        Payment payment = paymentService.release(request, clientId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    @PutMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin hoàn tiền Payment đang HELD", description = "Dùng khi xử lý Dispute")
    public ResponseEntity<ApiResponse<Payment>> refund(
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.refund(id, reason)));
    }
}