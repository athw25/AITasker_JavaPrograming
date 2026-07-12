package com.aitasker.payment.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.payment.entity.Payment;
import com.aitasker.payment.entity.Transaction;
import com.aitasker.payment.entity.Withdrawal;
import com.aitasker.payment.enums.WithdrawalStatus;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.payment.service.EscrowService;
import com.aitasker.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    private final PaymentRepository paymentRepository;

    @GetMapping
    @Operation(summary = "Admin xem tất cả Payment")
    public ApiResponse<List<Payment>> getAllPayments() {
        return ApiResponse.success(paymentRepository.findAll());
    }

    @GetMapping("/transactions")
    @Operation(summary = "Admin xem tất cả Transaction")
    public ApiResponse<List<Transaction>> getAllTransactions() {
        return ApiResponse.success(paymentService.getAllTransactions());
    }

    @GetMapping("/withdrawals")
    @Operation(summary = "Admin xem Withdrawal theo status")
    public ApiResponse<List<Withdrawal>> getWithdrawals(
            @RequestParam(defaultValue = "PENDING") WithdrawalStatus status) {
        return ApiResponse.success(escrowService.getWithdrawalsByStatus(status));
    }

    @PutMapping("/withdrawals/{id}/approve")
    @Operation(summary = "Admin duyệt Withdrawal")
    public ApiResponse<Withdrawal> approveWithdrawal(@PathVariable Long id) {
        return ApiResponse.success(escrowService.approveWithdrawal(id));
    }
}
