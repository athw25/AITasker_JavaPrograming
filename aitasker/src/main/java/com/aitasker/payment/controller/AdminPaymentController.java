package com.aitasker.payment.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.payment.entity.*;
import com.aitasker.payment.enums.WithdrawalStatus;
import com.aitasker.payment.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Payment", description = "Admin xem lịch sử giao dịch và duyệt withdrawal")
public class AdminPaymentController {

    private final PaymentService paymentService;
    private final EscrowService escrowService;

    @GetMapping("/transactions")
    @Operation(summary = "Admin xem tất cả Transaction (read-only)")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAllTransactions() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllTransactions()));
    }

    @GetMapping("/withdrawals")
    @Operation(summary = "Admin xem danh sách Withdrawal theo status")
    public ResponseEntity<ApiResponse<List<Withdrawal>>> getWithdrawals(
            @RequestParam(defaultValue = "PENDING") WithdrawalStatus status
    ) {
        return ResponseEntity.ok(ApiResponse.success(escrowService.getWithdrawalsByStatus(status)));
    }

    @PutMapping("/withdrawals/{id}/approve")
    @Operation(summary = "Admin duyệt Withdrawal")
    public ResponseEntity<ApiResponse<Withdrawal>> approveWithdrawal(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(escrowService.approveWithdrawal(id)));
    }
}