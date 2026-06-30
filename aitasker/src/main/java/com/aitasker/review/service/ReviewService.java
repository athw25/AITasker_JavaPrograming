package com.aitasker.review.service;

import com.aitasker.common.enums.ProjectStatus;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public ReviewResponse create(ReviewRequest request) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User reviewer = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found.")
                );

        Project project = projectRepository
                .findById(request.getProjectId())
                .orElseThrow(() ->
                        new RuntimeException("Project not found.")
                );

        /*
         * Rule 1:
         * Project phải COMPLETED.
         */
        if (project.getStatus() != ProjectStatus.COMPLETED) {
            throw new RuntimeException(
                    "Reviews can only be created after project completion."
            );
        }

        /*
         * Rule 2:
         * Reviewer phải là người tham gia Project.
         */
        boolean isParticipant =
                project.getClient().getId().equals(reviewer.getId())
                        || project.getExpert().getId().equals(reviewer.getId());

        if (!isParticipant) {
            throw new RuntimeException(
                    "Only project participants can leave a review."
            );
        }

        /*
         * Rule 3:
         * Không được review 2 lần.
         */
        boolean alreadyReviewed =
                reviewRepository.existsByReviewerIdAndProjectId(
                        reviewer.getId(),
                        project.getId()
                );

        if (alreadyReviewed) {
            throw new RuntimeException(
                    "You have already reviewed this project."
            );
        }

        User reviewee = userRepository
                .findById(request.getRevieweeId())
                .orElseThrow(() ->
                        new RuntimeException("Reviewee not found.")
                );

        /*
         * Rule 4:
         * Không được tự review chính mình.
         */
        if (reviewee.getId().equals(reviewer.getId())) {
            throw new RuntimeException(
                    "You cannot review yourself."
            );
        }

        /*
         * Rule 5:
         * Reviewee phải thuộc Project.
         */
        boolean validReviewee =
                project.getClient().getId().equals(reviewee.getId())
                        || project.getExpert().getId().equals(reviewee.getId());

        if (!validReviewee) {
            throw new RuntimeException(
                    "Reviewee is not a participant of this project."
            );
        }

        Review review = new Review();

        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setProject(project);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setType(request.getType());
        review.setCreatedAt(LocalDateTime.now());

        review = reviewRepository.save(review);

        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getByUser(Long userId) {
        return reviewRepository
                .findByRevieweeId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewResponse toResponse(Review review) {

        ReviewResponse response =
                new ReviewResponse();

        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setType(review.getType());
        response.setCreatedAt(review.getCreatedAt());

        if (review.getReviewer() != null) {
            response.setReviewerId(
                    review.getReviewer().getId()
            );
            response.setReviewerName(
                    review.getReviewer().getName()
            );
        }

        if (review.getReviewee() != null) {
            response.setRevieweeId(
                    review.getReviewee().getId()
            );
            response.setRevieweeName(
                    review.getReviewee().getName()
            );
        }

        if (review.getProject() != null) {
            response.setProjectId(
                    review.getProject().getId()
            );
        }

        return response;
    }
}