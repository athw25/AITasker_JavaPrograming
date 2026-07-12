package com.aitasker.payment.service;

import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.exception.BadRequestException;
import com.aitasker.payment.dto.WithdrawalRequest;
import com.aitasker.payment.entity.Withdrawal;
import com.aitasker.payment.enums.WithdrawalStatus;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.payment.repository.TransactionRepository;
import com.aitasker.payment.repository.WithdrawalRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscrowServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private WithdrawalRepository withdrawalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private EscrowService escrowService;

    private User expert;

    @BeforeEach
    void setUp() {
        expert = new User();
        expert.setId(1L);
    }

    @Test
    void requestWithdrawal_shouldSucceed_whenAmountWithinAvailableBalance() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAmount(new BigDecimal("100"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(expert));
        when(paymentRepository.getReleasedAmountForExpert(1L)).thenReturn(new BigDecimal("500"));
        when(withdrawalRepository.getReservedOrWithdrawnAmount(1L)).thenReturn(new BigDecimal("200"));
        when(withdrawalRepository.save(any(Withdrawal.class))).thenAnswer(invocation -> {
            Withdrawal w = invocation.getArgument(0);
            w.setId(10L);
            return w;
        });

        Withdrawal result = escrowService.requestWithdrawal(request, 1L);

        assertEquals(WithdrawalStatus.PENDING, result.getStatus());
        verify(withdrawalRepository).save(any(Withdrawal.class));
    }

    @Test
    void requestWithdrawal_shouldReject_whenAmountExceedsAvailableBalance() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAmount(new BigDecimal("1000000"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(expert));
        when(paymentRepository.getReleasedAmountForExpert(1L)).thenReturn(new BigDecimal("500"));
        when(withdrawalRepository.getReservedOrWithdrawnAmount(1L)).thenReturn(BigDecimal.ZERO);

        assertThrows(BadRequestException.class, () -> escrowService.requestWithdrawal(request, 1L));
        verify(withdrawalRepository, never()).save(any());
    }

    @Test
    void requestWithdrawal_shouldReject_whenAmountIsZeroOrNegative() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAmount(BigDecimal.ZERO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(expert));

        assertThrows(BadRequestException.class, () -> escrowService.requestWithdrawal(request, 1L));
        verify(withdrawalRepository, never()).save(any());
    }

    @Test
    void getAvailableBalance_shouldEqualReleasedMinusReserved() {
        when(paymentRepository.getReleasedAmountForExpert(1L)).thenReturn(new BigDecimal("300"));
        when(withdrawalRepository.getReservedOrWithdrawnAmount(1L)).thenReturn(new BigDecimal("50"));

        BigDecimal balance = escrowService.getAvailableBalance(1L);

        assertEquals(new BigDecimal("250"), balance);
    }

    @Test
    void approveWithdrawal_shouldReject_whenNotEnoughBalanceRemaining() {
        Withdrawal withdrawal = Withdrawal.builder()
                .expert(expert)
                .amount(new BigDecimal("100"))
                .status(WithdrawalStatus.PENDING)
                .build();
        withdrawal.setId(10L);

        when(withdrawalRepository.findById(10L)).thenReturn(Optional.of(withdrawal));
        when(paymentRepository.getReleasedAmountForExpert(1L)).thenReturn(new BigDecimal("100"));
        // Đã có 150 đang PENDING/APPROVED (bao gồm chính withdrawal này) => sau khi trừ còn 50 < 100 amount
        when(withdrawalRepository.getReservedOrWithdrawnAmount(1L)).thenReturn(new BigDecimal("150"));

        assertThrows(BadRequestException.class, () -> escrowService.approveWithdrawal(10L));
        verify(withdrawalRepository, never()).save(any());
    }

    @Test
    void approveWithdrawal_shouldReject_whenNotPending() {
        Withdrawal withdrawal = Withdrawal.builder()
                .expert(expert)
                .amount(new BigDecimal("100"))
                .status(WithdrawalStatus.APPROVED)
                .build();
        withdrawal.setId(10L);

        when(withdrawalRepository.findById(10L)).thenReturn(Optional.of(withdrawal));

        assertThrows(BadRequestException.class, () -> escrowService.approveWithdrawal(10L));
    }
}
