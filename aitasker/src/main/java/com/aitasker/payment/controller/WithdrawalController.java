package com.aitasker.payment.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.payment.dto.WithdrawalRequest;
import com.aitasker.payment.entity.Withdrawal;
import com.aitasker.payment.service.EscrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
@Tag(name = "Withdrawal", description = "API quản lý rút tiền của Expert")
public class WithdrawalController {

    private final EscrowService escrowService;

    @PostMapping
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Tạo yêu cầu rút tiền")
    public ResponseEntity<ApiResponse<Withdrawal>> requestWithdrawal(
            @Valid @RequestBody WithdrawalRequest request
    ) {
        Withdrawal withdrawal = escrowService.requestWithdrawal(request, 1L); // thay bằng expertId từ JWT
        return ResponseEntity.ok(ApiResponse.success(withdrawal));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Expert xem danh sách withdrawal của mình")
    public ResponseEntity<ApiResponse<List<Withdrawal>>> getMyWithdrawals() {
        List<Withdrawal> list = escrowService.getMyWithdrawals(1L); // expertId từ JWT
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}