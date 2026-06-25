package com.aitasker.review.service;

import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.review.dto.ReviewRequest;
import com.aitasker.review.dto.ReviewResponse;
import com.aitasker.review.entity.Review;
import com.aitasker.review.repository.ReviewRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ReviewResponse create(ReviewRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User reviewer = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // bussiness rule: chi nguoi tham gia project COMPLETED moi duoc review
        boolean isParticipant = project.getClient().getId().equals(reviewer.getId())
                || project.getExpert().getId().equals(reviewer.getId());
        if(!isParticipant){
            throw new RuntimeException("Only project participants can leave a review");
        }
        // khong duoc review 2 lan cung 1 project
        if(reviewRepository.existsByReviewerIdAndProjectId(reviewer.getId(), request.getProjectId())){
            throw new RuntimeException("You have already reviewed this project");
        }
        User reviewee = userRepository.findById(request.getRevieweeId())
                .orElseThrow(() -> new RuntimeException("Reviewee not found"));

        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setProject(project);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setType(request.getType());
        review.setCreatedAt(LocalDateTime.now());

        return toResponse(reviewRepository.save(review));
    }
    public List<ReviewResponse> getByUser(Long userId){
        return reviewRepository.findByRevieweeId(userId).stream().map(this::toResponse).toList();
    }
    private ReviewResponse toResponse(Review review){
        ReviewResponse res = new ReviewResponse();
        res.setId(review.getId());
        res.setRating(review.getRating());
        res.setComment(review.getComment());
        res.setType(review.getType());
        res.setCreatedAt(review.getCreatedAt());
        if(review.getReviewer() != null){
            res.setReviewerId(review.getReviewer().getId());
            res.setReviewerName(review.getReviewer().getName());
        }if(review.getReviewee() != null){
            res.setRevieweeId(review.getReviewee().getId());
            res.setRevieweeName(review.getReviewee().getName());
        }
        if(review.getProject() != null){
            res.setProjectId(review.getProject().getId());
        }
        return res;
    }

}
