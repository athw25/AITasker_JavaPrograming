package com.aitasker.payment.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.payment.dto.WithdrawalRequest;
import com.aitasker.payment.entity.Withdrawal;
import com.aitasker.payment.service.EscrowService;
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

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
@Tag(name = "Withdrawal", description = "API quản lý rút tiền của Expert")
public class WithdrawalController {

    private final EscrowService escrowService;

    @PostMapping
    @PreAuthorize("hasRole('AI_EXPERT')")
    @Operation(summary = "Tạo yêu cầu rút tiền")
    public ResponseEntity<ApiResponse<Withdrawal>> requestWithdrawal(
            @Valid @RequestBody WithdrawalRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long expertId = principal.getUser().getId();
        Withdrawal withdrawal = escrowService.requestWithdrawal(request, expertId);
        return ResponseEntity.ok(ApiResponse.success(withdrawal));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('AI_EXPERT')")
    @Operation(summary = "Expert xem danh sách withdrawal của mình")
    public ResponseEntity<ApiResponse<List<Withdrawal>>> getMyWithdrawals(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Long expertId = principal.getUser().getId();
        List<Withdrawal> list = escrowService.getMyWithdrawals(expertId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}