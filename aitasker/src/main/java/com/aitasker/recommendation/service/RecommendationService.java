package com.aitasker.recommendation.service;

import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.recommendation.dto.response.RecommendationResponseDTO;
import com.aitasker.recommendation.entity.Recommendation;
import com.aitasker.recommendation.repository.RecommendationRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import com.aitasker.common.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;

    // 1. CẬP NHẬT FEEDBACK KHI CLIENT THUÊ EXPERT (Gọi từ ProposalService)
    @Transactional
    public void markAsAccepted(Long jobId, Long expertId) {
        recommendationRepository.findByJobIdAndExpertId(jobId, expertId).ifPresent(rec -> {
            rec.setAccepted(true);
            recommendationRepository.save(rec);
            log.info("Cập nhật Analytics RQ1: Client đã thuê Expert {} cho Job {}.", expertId, jobId);
        });
    }

    // 2. THUẬT TOÁN ĐỀ XUẤT EXPERT CHO MỘT JOB
    @Transactional
    public List<RecommendationResponseDTO> recommendExpertsForJob(Long jobId) {
        JobPost job = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Job ID: " + jobId));

        // Lấy danh sách toàn bộ Expert trên hệ thống
        List<User> allExperts = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.EXPERT)
                .collect(Collectors.toList());

        List<Recommendation> recommendations = new ArrayList<>();

        for (User expert : allExperts) {
            // Áp dụng công thức: 40% Skills + 20% Rating + 20% Success Rate + 20% Experience
            double skillScore = calculateSkillMatch(job.getRequiredSkills(), expert) * 0.4;
            double ratingScore = getExpertRating(expert) * 0.2;
            double successRateScore = getExpertSuccessRate(expert) * 0.2;
            double experienceScore = getExpertExperience(expert) * 0.2;

            double totalMatchScore = skillScore + ratingScore + successRateScore + experienceScore;

            // Lọc những người có độ phù hợp trên 50%
            if (totalMatchScore >= 50.0) {
                Recommendation rec = recommendationRepository.findByJobIdAndExpertId(jobId, expert.getId())
                        .orElse(new Recommendation());

                rec.setJob(job);
                rec.setExpert(expert);
                rec.setMatchScore(Math.round(totalMatchScore * 100.0) / 100.0); // Làm tròn 2 chữ số

                recommendations.add(rec);
            }
        }

        // Lưu toàn bộ lịch sử gợi ý xuống Database để đo lường
        recommendationRepository.saveAll(recommendations);
        log.info("Đã sinh và lưu {} gợi ý chuyên gia cho Job ID: {}", recommendations.size(), jobId);

        // Sắp xếp điểm từ cao xuống thấp và map ra DTO trả về cho Client
        return recommendations.stream()
                .sorted(Comparator.comparing(Recommendation::getMatchScore).reversed())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 3. XEM LỊCH SỬ FEEDBACK CỦA MỘT JOB
    public List<RecommendationResponseDTO> getRecommendationHistory(Long jobId) {
        return recommendationRepository.findByJobId(jobId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private double calculateSkillMatch(String requiredSkills, User expert) {
        // Tạm thời trả về ngẫu nhiên để test luồng, thay bằng logic so sánh chuỗi thật
        return 80.0 + Math.random() * 20.0;
    }
    private double getExpertRating(User expert) { return 90.0; } // Giả lập Rating 4.5/5 -> 90 điểm
    private double getExpertSuccessRate(User expert) { return 95.0; }
    private double getExpertExperience(User expert) { return 85.0; }

    private RecommendationResponseDTO mapToDTO(Recommendation rec) {
        return RecommendationResponseDTO.builder()
                .recommendationId(rec.getId())
                .jobId(rec.getJob().getId())
                .expertId(rec.getExpert().getId())
                .expertName(rec.getExpert().getName())
                .matchScore(rec.getMatchScore())
                .isAccepted(rec.isAccepted())
                .createdAt(rec.getCreatedAt())
                .build();
    }
    // 4. LẤY TOÀN BỘ LỊCH SỬ GỢI Ý (Phục vụ API /api/recommendations cho RQ1)
    public List<RecommendationResponseDTO> getAllRecommendations() {
        return recommendationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}