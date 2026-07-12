package com.aitasker.review.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.review.dto.ReviewRequest;
import com.aitasker.review.dto.ReviewResponse;
import com.aitasker.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review System")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a review")
    public ApiResponse<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(reviewService.create(request));
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get reviews for a user")
    public ApiResponse<List<ReviewResponse>> getByUser(@PathVariable Long id) {
        return ApiResponse.success(reviewService.getByUser(id));
    }
}
