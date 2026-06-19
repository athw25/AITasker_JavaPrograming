package com.aitasker.review.controller;

import com.aitasker.review.dto.ReviewRequest;
import com.aitasker.review.dto.ReviewResponse;
import com.aitasker.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ReviewResponse> create(@RequestBody ReviewRequest request){
        return ResponseEntity.ok(reviewService.create(request));
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "Get reviews for a user")
    public ResponseEntity<List<ReviewResponse>> getByUser (@PathVariable Long id){
        return ResponseEntity.ok(reviewService.getByUser(id));
    }
}
