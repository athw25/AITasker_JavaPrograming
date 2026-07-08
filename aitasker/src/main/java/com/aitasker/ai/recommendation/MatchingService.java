package com.aitasker.ai.recommendation;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.expert.entity.ExpertProfile;
import com.aitasker.job.entity.JobPost;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final ProjectRepository projectRepository;
    private final ReviewRepository reviewRepository;

    public double computeMatchScore(JobPost job, ExpertProfile profile) {
        double skillScore = skillOverlap(job.getRequiredSkills(), profile.getSkills()) * 50;
        double ratingScore = averageRating(profile.getUser().getId()) / 5.0 * 30;
        double successScore = successRate(profile.getUser().getId()) * 15;
        double experienceScore = Math.min(profile.getExperienceYears(), 10) / 10.0 * 5;

        return skillScore + ratingScore + successScore + experienceScore;
    }

    private double skillOverlap(String requiredSkills, String expertSkills) {
        if (requiredSkills == null || requiredSkills.isBlank()
                || expertSkills == null || expertSkills.isBlank()) {
            return 0;
        }
        Set<String> required = toSkillSet(requiredSkills);
        Set<String> owned = toSkillSet(expertSkills);
        if (required.isEmpty()) return 0;

        long matched = required.stream().filter(owned::contains).count();
        return (double) matched / required.size();
    }

    private Set<String> toSkillSet(String skills) {
        return Arrays.stream(skills.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private double averageRating(Long userId) {
        List<com.aitasker.review.entity.Review> reviews = reviewRepository.findByRevieweeId(userId);
        if (reviews.isEmpty()) return 0;
        return reviews.stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(com.aitasker.review.entity.Review::getRating)
                .average()
                .orElse(0);
    }

    private double successRate(Long expertId) {
        long total = projectRepository.countByExpert_Id(expertId);
        if (total == 0) return 0;
        long completed = projectRepository.countByExpert_IdAndStatus(expertId, ProjectStatus.COMPLETED);
        return (double) completed / total;
    }
}
