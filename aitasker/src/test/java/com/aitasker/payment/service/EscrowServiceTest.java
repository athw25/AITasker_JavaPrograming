package com.aitasker.payment.service;

import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.Role;
import com.aitasker.exception.BadRequestException;
import com.aitasker.payment.dto.WithdrawalRequest;
import com.aitasker.payment.entity.Withdrawal;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscrowServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private WithdrawalRepository withdrawalRepository;
    @Mock private UserRepository userRepository;
    @Mock private AnalyticsService analyticsService;

    @InjectMocks
    private EscrowService escrowService;

    private User expert;

    @BeforeEach
    void setUp() {
        expert = new User();
        expert.setId(1L);
        expert.setRole(Role.EXPERT);
    }

    @Test
    void requestWithdrawal_vuotSoDuKhaDung_biTuChoi() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAmount(BigDecimal.valueOf(1000));

        when(userRepository.findById(1L)).thenReturn(Optional.of(expert));
        when(paymentRepository.getTotalReleasedForExpert(1L)).thenReturn(BigDecimal.valueOf(800));
        when(withdrawalRepository.getTotalRequestedOrApprovedForExpert(1L)).thenReturn(BigDecimal.ZERO);

        assertThatThrownBy(() -> escrowService.requestWithdrawal(request, 1L))
                .isInstanceOf(BadRequestException.class);

        verify(withdrawalRepository, never()).save(any());
    }

    @Test
    void requestWithdrawal_trongSoDu_thanhCong() {
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAmount(BigDecimal.valueOf(300));

        when(userRepository.findById(1L)).thenReturn(Optional.of(expert));
        when(paymentRepository.getTotalReleasedForExpert(1L)).thenReturn(BigDecimal.valueOf(800));
        when(withdrawalRepository.getTotalRequestedOrApprovedForExpert(1L)).thenReturn(BigDecimal.valueOf(400));
        when(withdrawalRepository.save(any(Withdrawal.class))).thenAnswer(inv -> inv.getArgument(0));

        escrowService.requestWithdrawal(request, 1L);

        verify(withdrawalRepository).save(any(Withdrawal.class));
        verify(analyticsService).recordEvent(any(), eq(1L), any(), any(), any());
    }
}
