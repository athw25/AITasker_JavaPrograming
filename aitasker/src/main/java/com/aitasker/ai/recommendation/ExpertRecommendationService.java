package com.aitasker.ai.recommendation;

import com.aitasker.ai.dto.response.ExpertRecommendationResponse;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.expert.entity.ExpertProfile;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpertRecommendationService {

    private final JobPostRepository jobPostRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final MatchingService matchingService;

    public List<ExpertRecommendationResponse> recommend(Long jobId) {
        JobPost job = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Job"));

        List<ExpertProfile> experts = expertProfileRepository.findAll();

        return experts.stream()
                .filter(e -> e.getUser() != null)
                .map(e -> new ExpertRecommendationResponse(
                        e.getUser().getId(),
                        e.getFullName(),
                        Math.round(matchingService.computeMatchScore(job, e) * 100.0) / 100.0
                ))
                .sorted(Comparator.comparingDouble(ExpertRecommendationResponse::getMatchScore).reversed())
                .toList();
    }
}
