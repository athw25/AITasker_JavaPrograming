package com.aitasker.recommendation.service;

import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.common.enums.Role;
import com.aitasker.common.response.PageResponse;
import com.aitasker.expert.entity.ExpertProfile;
import com.aitasker.expert.repository.ExpertProfileRepository;
import com.aitasker.expert.repository.PortfolioRepository;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.recommendation.dto.response.RecommendationAnalyticsResponse;
import com.aitasker.recommendation.dto.response.RecommendationResponseDTO;
import com.aitasker.recommendation.entity.Recommendation;
import com.aitasker.recommendation.repository.RecommendationRepository;
import com.aitasker.review.repository.ReviewRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final ExpertProfileRepository expertProfileRepository;
    private final ReviewRepository reviewRepository;
    private final ProjectRepository projectRepository;
    private final PortfolioRepository portfolioRepository;
    private final ProposalRepository proposalRepository;
    private final AnalyticsService analyticsService;

    // 1. CẬP NHẬT FEEDBACK KHI CLIENT THUÊ EXPERT
    @Transactional
    public void markAsAccepted(Long jobId, Long expertId) {
        recommendationRepository.findByJobIdAndExpertId(jobId, expertId).ifPresent(rec -> {
            rec.setAccepted(true);
            recommendationRepository.save(rec);
            log.info("Analytics RQ1: Client đã thuê Expert {} cho Job {}.", expertId, jobId);
        });
    }

    // 2. THUẬT TOÁN ĐỀ XUẤT EXPERT CHO MỘT JOB (Sử dụng 5 chỉ số thực tế)
    @Transactional
    public List<RecommendationResponseDTO> recommendExpertsForJob(Long jobId) {
        JobPost job = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Job ID: " + jobId));

        // Bảo mật kiểm tra quyền sở hữu công việc
        checkJobOwnership(job);

        // Lấy danh sách hồ sơ chuyên gia tối ưu
        List<ExpertProfile> allExpertProfiles = expertProfileRepository.findByRoleWithUser(Role.EXPERT);

        // Nạp trước dữ liệu dạng Batch tránh lỗi N+1
        Map<Long, Double> averageRatings = fetchBatchAverageRatings();
        Map<Long, Long> totalProjects = fetchBatchTotalProjects();
        Map<Long, Long> completedProjects = fetchBatchCompletedProjects();
        Map<Long, Long> portfolioCounts = fetchBatchPortfolioCounts();

        List<Recommendation> recommendations = new ArrayList<>();

        for (ExpertProfile profile : allExpertProfiles) {
            User expert = profile.getUser();

            // Tính điểm các thành phần
            double skillScore = calculateSkillMatch(job.getRequiredSkills(), profile.getSkills());
            double ratingScore = calculateRatingScore(expert.getId(), averageRatings);
            double successRateScore = calculateSuccessRateScore(expert.getId(), totalProjects, completedProjects);
            double experienceScore = calculateExperienceScore(profile);
            double portfolioScore = calculatePortfolioScore(expert.getId(), portfolioCounts);

            // Công thức áp dụng: 30% Skills + 20% Rating + 20% Success Rate + 15% Experience + 15% Portfolio
            double totalMatchScore = (skillScore * 0.3)
                    + (ratingScore * 0.2)
                    + (successRateScore * 0.2)
                    + (experienceScore * 0.15)
                    + (portfolioScore * 0.15);

            // Đạt trên 50 điểm mới tiến hành lưu gợi ý
            if (totalMatchScore >= 50.0) {
                Recommendation rec = recommendationRepository.findByJobIdAndExpertId(jobId, expert.getId())
                        .orElse(new Recommendation());

                rec.setJob(job);
                rec.setExpert(expert);
                rec.setMatchScore(Math.round(totalMatchScore * 100.0) / 100.0);
                rec.setSkillScore(Math.round(skillScore * 100.0) / 100.0);
                rec.setRatingScore(Math.round(ratingScore * 100.0) / 100.0);
                rec.setSuccessRateScore(Math.round(successRateScore * 100.0) / 100.0);
                rec.setExperienceScore(Math.round(experienceScore * 100.0) / 100.0);
                rec.setPortfolioScore(Math.round(portfolioScore * 100.0) / 100.0);

                recommendations.add(rec);
            }
        }

        recommendationRepository.saveAll(recommendations);
        log.info("Đã lưu {} gợi ý chuyên gia cho Job ID: {}", recommendations.size(), jobId);

        for (Recommendation rec : recommendations) {
            analyticsService.recordEvent(AnalyticsEventType.EXPERT_RECOMMENDED, rec.getExpert().getId(),
                    "EXPERT", "Job", jobId.toString());
        }

        return recommendations.stream()
                .sorted(Comparator.comparing(Recommendation::getMatchScore).reversed())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 3. XEM LỊCH SỬ GỢI Ý CỦA MỘT JOB
    @Transactional(readOnly = true)
    public List<RecommendationResponseDTO> getRecommendationHistory(Long jobId) {
        JobPost job = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Job ID: " + jobId));

        checkJobOwnership(job);

        return recommendationRepository.findByJobId(jobId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // 4. LẤY DANH SÁCH PHÂN TRANG (ADMIN)
    @Transactional(readOnly = true)
    public PageResponse<RecommendationResponseDTO> getAllRecommendations(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Recommendation> recPage = recommendationRepository.findAllWithJobAndExpert(pageable);

        List<RecommendationResponseDTO> content = recPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                recPage.getNumber(),
                recPage.getSize(),
                recPage.getTotalElements(),
                recPage.getTotalPages(),
                recPage.isFirst(),
                recPage.isLast()
        );
    }

    // 5. THỐNG KÊ ANALYTICS RQ1
    @Transactional(readOnly = true)
    public RecommendationAnalyticsResponse getAnalytics() {
        long total = recommendationRepository.countOverall();
        long accepted = recommendationRepository.countAcceptedOverall();
        double acceptanceRate = total > 0 ? ((double) accepted / total) * 100.0 : 0.0;

        // Tính Proposal Conversion Rate hệ thống
        long totalProposals = proposalRepository.countTotalProposals();
        long acceptedProposals = proposalRepository.countAcceptedProposals();
        double proposalConversionRate = totalProposals > 0 ? ((double) acceptedProposals / totalProposals) * 100.0 : 0.0;

        Double avgOverall = recommendationRepository.getAverageMatchScoreOverall();
        Double avgAccepted = recommendationRepository.getAverageMatchScoreOfAccepted();

        // Độ chính xác khuyến nghị (Đo bằng độ lệch điểm trung bình được chấp nhận so với thang điểm 100)
        double recAccuracy = avgAccepted != null ? avgAccepted : 0.0;

        return RecommendationAnalyticsResponse.builder()
                .totalRecommendations(total)
                .totalAcceptedRecommendations(accepted)
                .acceptanceRate(Math.round(acceptanceRate * 100.0) / 100.0)
                .proposalConversionRate(Math.round(proposalConversionRate * 100.0) / 100.0)
                .recommendationAccuracy(Math.round(recAccuracy * 100.0) / 100.0)
                .averageMatchScoreOverall(avgOverall != null ? Math.round(avgOverall * 100.0) / 100.0 : 0.0)
                .averageMatchScoreOfAccepted(avgAccepted != null ? Math.round(avgAccepted * 100.0) / 100.0 : 0.0)
                .build();
    }

    // --- LOGIC CHI TIẾT TỪNG PHẦN ---

    private double calculateSkillMatch(String requiredSkills, String expertSkills) {
        if (requiredSkills == null || requiredSkills.isBlank()) return 100.0;
        if (expertSkills == null || expertSkills.isBlank()) return 0.0;

        Set<String> reqSet = Stream.of(requiredSkills.split(","))
                .map(String::trim).map(String::toLowerCase).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
        Set<String> expSet = Stream.of(expertSkills.split(","))
                .map(String::trim).map(String::toLowerCase).filter(s -> !s.isEmpty()).collect(Collectors.toSet());

        if (reqSet.isEmpty()) return 100.0;

        long matchedCount = reqSet.stream().filter(expSet::contains).count();
        return ((double) matchedCount / reqSet.size()) * 100.0;
    }

    private double calculateRatingScore(Long expertId, Map<Long, Double> averageRatings) {
        Double avgRating = averageRatings.get(expertId);
        return avgRating == null ? 80.0 : (avgRating / 5.0) * 100.0;
    }

    private double calculateSuccessRateScore(Long expertId, Map<Long, Long> totalProjects, Map<Long, Long> completedProjects) {
        long total = totalProjects.getOrDefault(expertId, 0L);
        long completed = completedProjects.getOrDefault(expertId, 0L);
        return total == 0 ? 100.0 : ((double) completed / total) * 100.0;
    }

    private double calculateExperienceScore(ExpertProfile profile) {
        // Tối đa 5 năm đạt 100 điểm
        return Math.min(profile.getExperienceYears(), 5) * 20.0;
    }

    private double calculatePortfolioScore(Long expertId, Map<Long, Long> portfolioCounts) {
        long count = portfolioCounts.getOrDefault(expertId, 0L);
        // Có 5 sản phẩm nổi bật trở lên đạt 100 điểm
        return Math.min(count, 5) * 20.0;
    }

    // --- BATCH FETCH HELPERS ---

    private Map<Long, Double> fetchBatchAverageRatings() {
        return reviewRepository.getAverageRatingsForExperts().stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> row[1] != null ? ((Number) row[1]).doubleValue() : 0.0
                ));
    }

    private Map<Long, Long> fetchBatchTotalProjects() {
        return projectRepository.countTotalProjectsGroupByExpert().stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> row[1] != null ? ((Number) row[1]).longValue() : 0L
                ));
    }

    private Map<Long, Long> fetchBatchCompletedProjects() {
        return projectRepository.countCompletedProjectsGroupByExpert().stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> row[1] != null ? ((Number) row[1]).longValue() : 0L
                ));
    }

    private Map<Long, Long> fetchBatchPortfolioCounts() {
        return portfolioRepository.getPortfolioCountsForExperts().stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> row[1] != null ? ((Number) row[1]).longValue() : 0L
                ));
    }

    private void checkJobOwnership(JobPost job) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng hiện tại"));

        if (currentUser.getRole() != Role.ADMIN && !job.getClient().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền xem thông tin đề xuất của công việc này.");
        }
    }

    private RecommendationResponseDTO mapToDTO(Recommendation rec) {
        return RecommendationResponseDTO.builder()
                .recommendationId(rec.getId())
                .jobId(rec.getJob().getId())
                .expertId(rec.getExpert().getId())
                .expertName(rec.getExpert().getName())
                .matchScore(rec.getMatchScore())
                .skillScore(rec.getSkillScore())
                .ratingScore(rec.getRatingScore())
                .successRateScore(rec.getSuccessRateScore())
                .experienceScore(rec.getExperienceScore())
                .portfolioScore(rec.getPortfolioScore())
                .isAccepted(rec.isAccepted())
                .createdAt(rec.getCreatedAt())
                .build();
    }
}
