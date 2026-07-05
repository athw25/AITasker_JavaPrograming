package com.aitasker.review.service;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.exception.ResourceNotFoundException;
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
                        new ResourceNotFoundException("Không tìm thấy người dùng")
                );

        Project project = projectRepository
                .findById(request.getProjectId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy Project")
                );

        /*
         * Rule 1:
         * Project phải COMPLETED.
         */
        if (project.getStatus() != ProjectStatus.COMPLETED) {
            throw new com.aitasker.exception.BadRequestException(
                    "Chỉ có thể review khi Project đã hoàn thành"
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
            throw new com.aitasker.exception.ForbiddenException(
                    "Chỉ người tham gia Project mới được review"
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
            throw new com.aitasker.exception.BusinessException(
                    "Bạn đã review Project này rồi"
            );
        }

        User reviewee = userRepository
                .findById(request.getRevieweeId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy Reviewee")
                );

        /*
         * Rule 4:
         * Không được tự review chính mình.
         */
        if (reviewee.getId().equals(reviewer.getId())) {
            throw new com.aitasker.exception.ForbiddenException(
                    "Không thể tự review chính mình"
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
            throw new com.aitasker.exception.ForbiddenException(
                    "Reviewee không phải thành viên của Project này"
            );
        }

        Review review = new Review();

        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review.setProject(project);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setType(request.getType());

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