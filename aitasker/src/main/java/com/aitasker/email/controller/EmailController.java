package com.aitasker.email.controller;

import com.aitasker.email.dto.EmailRequest;
import com.aitasker.email.service.EmailService;
import com.aitasker.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email Service", description = "Email notification endpoints")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/test")
    @Operation(summary = "Test email sending", description = "Send a test email to verify email configuration")
    public ResponseEntity<ApiResponse> sendTestEmail(@RequestBody EmailRequest request) {
        try {
            if ("proposal".equalsIgnoreCase(request.getType())) {
                emailService.sendProposalNotification(
                    request.getTo(),
                    "Test User",
                    "Test Project",
                    "$1000",
                    "http://example.com/proposal/1"
                );
            } else if ("payment".equalsIgnoreCase(request.getType())) {
                emailService.sendPaymentNotification(
                    request.getTo(),
                    "Test User",
                    "Project Payment",
                    "$500",
                    "TXN123456",
                    "SUCCESS"
                );
            } else if ("dispute".equalsIgnoreCase(request.getType())) {
                emailService.sendDisputeNotification(
                    request.getTo(),
                    "Test User",
                    "Test Project",
                    "Quality issue",
                    "OPENED",
                    "http://example.com/dispute/1"
                );
            } else if ("password-reset".equalsIgnoreCase(request.getType())) {
                emailService.sendPasswordResetEmail(
                    request.getTo(),
                    "Test User",
                    "http://example.com/reset?token=abc123",
                    "30"
                );
            } else {
                return ResponseEntity.ok(ApiResponse.fail("Invalid email type"));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Email sent successfully to: " + request.getTo()));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail("Failed to send email: " + e.getMessage()));
        }
    }

    @PostMapping("/proposal")
    @Operation(summary = "Send proposal notification")
    public ResponseEntity<ApiResponse> sendProposalEmail(
            @RequestParam String to,
            @RequestParam String recipientName,
            @RequestParam String projectTitle,
            @RequestParam String proposalAmount,
            @RequestParam String proposalLink) {
        try {
            emailService.sendProposalNotification(to, recipientName, projectTitle, proposalAmount, proposalLink);
            return ResponseEntity.ok(ApiResponse.success("Proposal email sent"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/payment")
    @Operation(summary = "Send payment notification")
    public ResponseEntity<ApiResponse> sendPaymentEmail(
            @RequestParam String to,
            @RequestParam String recipientName,
            @RequestParam String paymentType,
            @RequestParam String amount,
            @RequestParam String transactionId,
            @RequestParam String status) {
        try {
            emailService.sendPaymentNotification(to, recipientName, paymentType, amount, transactionId, status);
            return ResponseEntity.ok(ApiResponse.success("Payment email sent"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/dispute")
    @Operation(summary = "Send dispute notification")
    public ResponseEntity<ApiResponse> sendDisputeEmail(
            @RequestParam String to,
            @RequestParam String recipientName,
            @RequestParam String projectTitle,
            @RequestParam String disputeReason,
            @RequestParam String disputeStatus,
            @RequestParam String disputeLink) {
        try {
            emailService.sendDisputeNotification(to, recipientName, projectTitle, disputeReason, disputeStatus, disputeLink);
            return ResponseEntity.ok(ApiResponse.success("Dispute email sent"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Send password reset email")
    public ResponseEntity<ApiResponse> sendPasswordResetEmail(
            @RequestParam String to,
            @RequestParam String recipientName,
            @RequestParam String resetLink,
            @RequestParam String expirationTime) {
        try {
            emailService.sendPasswordResetEmail(to, recipientName, resetLink, expirationTime);
            return ResponseEntity.ok(ApiResponse.success("Password reset email sent"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.fail(e.getMessage()));
        }
    }
}
