package com.aitasker.delivery.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.delivery.dto.request.SubmitDeliveryRequest;
import com.aitasker.delivery.dto.response.DeliveryResponse;
import com.aitasker.delivery.service.DeliveryService;
import com.aitasker.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Deliveries", description = "Versioned milestone delivery APIs")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Submit a delivery")
    public ApiResponse<DeliveryResponse> submit(@Valid @RequestBody SubmitDeliveryRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Delivery submitted successfully",
                deliveryService.submitDelivery(request, principal.getUser()));
    }

    @GetMapping("/milestone/{id}")
    @Operation(summary = "Get a milestone's complete delivery history")
    public ApiResponse<List<DeliveryResponse>> byMilestone(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Deliveries retrieved successfully",
                deliveryService.getMilestoneDeliveries(id, principal.getUser()));
    }
}
