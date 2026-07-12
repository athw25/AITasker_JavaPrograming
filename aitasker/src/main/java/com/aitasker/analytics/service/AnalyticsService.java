package com.aitasker.analytics.service;

import com.aitasker.analytics.dto.AnalyticsResponse;
import com.aitasker.analytics.dto.DashboardSummaryResponse;
import com.aitasker.analytics.dto.MonthlyDataPoint;
import com.aitasker.analytics.dto.ReportResponse;
import com.aitasker.analytics.entity.AnalyticsEvent;
import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.repository.AnalyticsEventRepository;
import com.aitasker.analytics.repository.SystemMetricRepository;
import com.aitasker.ai.config.AiProperties;
import com.aitasker.ai.gateway.AiGateway;
import com.aitasker.common.enums.Role;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.user.entity.User;
import com.aitasker.payment.repository.PaymentRepository;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.proposal.repository.ProposalRepository;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AnalyticsEventRepository analyticsEventRepository;
    private final SystemMetricRepository systemMetricRepository;
    private final UserRepository userRepository;
    private final JobPostRepository jobPostRepository;
    private final ProjectRepository projectRepository;
    private final ProposalRepository proposalRepository;
    private final PaymentRepository paymentRepository;
    private final AiGateway aiGateway;
    private final AiProperties aiProperties;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public DashboardSummaryResponse getDashboard() {
        long totalUsers  = userRepository.count();
        long totalClients = userRepository.countByRole(Role.CLIENT);
        long totalExperts = userRepository.countByRole(Role.EXPERT);
        long totalJobs   = jobPostRepository.count();
        long totalProposals = proposalRepository.count();
        long totalProjects  = projectRepository.count();

        long completedProjects = analyticsEventRepository
                .countByEventType(AnalyticsEventType.PROJECT_COMPLETED);
        long activeProjects = analyticsEventRepository
                .countByEventType(AnalyticsEventType.PROJECT_STARTED)
                - completedProjects;
        activeProjects = Math.max(0, activeProjects);

        long activeJobs = analyticsEventRepository
                .countByEventType(AnalyticsEventType.JOB_CREATED)
                - analyticsEventRepository.countByEventType(AnalyticsEventType.JOB_CLOSED);
        activeJobs = Math.max(0, activeJobs);

        long proposalAccepted = analyticsEventRepository
                .countByEventType(AnalyticsEventType.PROPOSAL_ACCEPTED);
        double proposalAcceptanceRate = totalProposals > 0
                ? round((double) proposalAccepted / totalProposals * 100) : 0;
        double projectSuccessRate = totalProjects > 0
                ? round((double) completedProjects / totalProjects * 100) : 0;

        long aiPromptsUsed = analyticsEventRepository
                .countByEventType(AnalyticsEventType.AI_PROMPT_USED);
        long recommendationsGenerated = analyticsEventRepository
                .countByEventType(AnalyticsEventType.EXPERT_RECOMMENDED);

        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        BigDecimal revenueThisMonth = paymentRepository.getRevenueSince(monthStart);

        return DashboardSummaryResponse.builder()
                .totalUsers(totalUsers)
                .totalClients(totalClients)
                .totalExperts(totalExperts)
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .totalProposals(totalProposals)
                .totalProjects(totalProjects)
                .activeProjects(activeProjects)
                .completedProjects(completedProjects)
                .projectSuccessRate(projectSuccessRate)
                .proposalAcceptanceRate(proposalAcceptanceRate)
                .totalRevenue(totalRevenue)
                .revenueThisMonth(revenueThisMonth)
                .aiPromptsUsed(aiPromptsUsed)
                .recommendationsGenerated(recommendationsGenerated)
                .activeAiProvider(aiGateway.getActiveProviderName())
                .aiEnabled(aiProperties.isEnabled())
                .build();
    }

    public AnalyticsResponse getAnalytics() {
        long totalProposals = proposalRepository.count();
        long acceptedProposals = analyticsEventRepository
                .countByEventType(AnalyticsEventType.PROPOSAL_ACCEPTED);
        long totalProjects = projectRepository.count();
        long completedProjects = analyticsEventRepository
                .countByEventType(AnalyticsEventType.PROJECT_COMPLETED);
        long totalJobs = jobPostRepository.count();

        double proposalAcceptanceRate = totalProposals > 0
                ? round((double) acceptedProposals / totalProposals * 100) : 0;
        double projectSuccessRate = totalProjects > 0
                ? round((double) completedProjects / totalProjects * 100) : 0;
        double avgProposalsPerJob = totalJobs > 0
                ? round((double) totalProposals / totalJobs) : 0;

        long recommendationsGenerated = analyticsEventRepository
                .countByEventType(AnalyticsEventType.EXPERT_RECOMMENDED);
        long hired = analyticsEventRepository
                .countByEventType(AnalyticsEventType.PROPOSAL_ACCEPTED);
        double expertMatchConversionRate = recommendationsGenerated > 0
                ? round((double) hired / recommendationsGenerated * 100) : 0;

        List<Object[]> eventCounts = analyticsEventRepository
                .countGroupByEventTypeSince(LocalDateTime.now().minusDays(30));
        Map<String, Long> eventCountsByType = eventCounts.stream()
                .collect(Collectors.toMap(
                        row -> row[0].toString(),
                        row -> (Long) row[1]
                ));

        long jobAssistantPrompts = analyticsEventRepository
                .countByEventTypeAndCreatedAtBetween(
                        AnalyticsEventType.AI_PROMPT_USED,
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now()
                );

        return AnalyticsResponse.builder()
                .proposalAcceptanceRate(proposalAcceptanceRate)
                .projectSuccessRate(projectSuccessRate)
                .avgProposalsPerJob(avgProposalsPerJob)
                .expertMatchConversionRate(expertMatchConversionRate)
                .eventCountsByType(eventCountsByType)
                .topSkillsDemanded(List.of())
                .totalAiPromptsJobAssistant(jobAssistantPrompts)
                .totalAiPromptsServiceGenerator(0)
                .aiPromptAcceptanceRate(0)
                .build();
    }

    public ReportResponse getReports() {
        List<MonthlyDataPoint> monthlyNewUsers = buildMonthlyUserStats();
        List<MonthlyDataPoint> monthlyNewJobs  = buildMonthlyJobStats();
        List<MonthlyDataPoint> monthlyRevenue  = buildMonthlyRevenueStats();
        List<MonthlyDataPoint> monthlyCompleted = buildMonthlyEventStats(
                AnalyticsEventType.PROJECT_COMPLETED);
        List<MonthlyDataPoint> monthlyAiPrompts = buildMonthlyEventStats(
                AnalyticsEventType.AI_PROMPT_USED);

        return ReportResponse.builder()
                .monthlyRevenue(monthlyRevenue)
                .monthlyNewUsers(monthlyNewUsers)
                .monthlyNewJobs(monthlyNewJobs)
                .monthlyCompletedProjects(monthlyCompleted)
                .monthlyAiPrompts(monthlyAiPrompts)
                .build();
    }

    @Transactional
    public void recordEvent(AnalyticsEventType eventType, Long actorId,
                            String actorRole, String entityType, String entityId) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .eventType(eventType)
                .actorId(actorId)
                .actorRole(actorRole)
                .entityType(entityType)
                .entityId(entityId)
                .build();
        analyticsEventRepository.save(event);
    }

    private List<MonthlyDataPoint> buildMonthlyUserStats() {
        List<MonthlyDataPoint> result = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDateTime from = ym.atDay(1).atStartOfDay();
            LocalDateTime to   = ym.atEndOfMonth().atTime(23, 59, 59);
            long count = analyticsEventRepository
                    .countByEventTypeAndCreatedAtBetween(AnalyticsEventType.USER_REGISTERED, from, to);
            result.add(MonthlyDataPoint.builder()
                    .month(ym.format(MONTH_FORMATTER))
                    .count(count)
                    .amount(BigDecimal.ZERO)
                    .build());
        }
        return result;
    }

    private List<MonthlyDataPoint> buildMonthlyJobStats() {
        List<MonthlyDataPoint> result = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDateTime from = ym.atDay(1).atStartOfDay();
            LocalDateTime to   = ym.atEndOfMonth().atTime(23, 59, 59);
            long count = analyticsEventRepository
                    .countByEventTypeAndCreatedAtBetween(AnalyticsEventType.JOB_CREATED, from, to);
            result.add(MonthlyDataPoint.builder()
                    .month(ym.format(MONTH_FORMATTER))
                    .count(count)
                    .amount(BigDecimal.ZERO)
                    .build());
        }
        return result;
    }

    private List<MonthlyDataPoint> buildMonthlyEventStats(AnalyticsEventType type) {
        List<MonthlyDataPoint> result = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDateTime from = ym.atDay(1).atStartOfDay();
            LocalDateTime to   = ym.atEndOfMonth().atTime(23, 59, 59);
            long count = analyticsEventRepository
                    .countByEventTypeAndCreatedAtBetween(type, from, to);
            result.add(MonthlyDataPoint.builder()
                    .month(ym.format(MONTH_FORMATTER))
                    .count(count)
                    .amount(BigDecimal.ZERO)
                    .build());
        }
        return result;
    }

    private List<MonthlyDataPoint> buildMonthlyRevenueStats() {
        List<MonthlyDataPoint> result = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDateTime from = ym.atDay(1).atStartOfDay();
            LocalDateTime to   = ym.atEndOfMonth().atTime(23, 59, 59);
            BigDecimal amount = paymentRepository.getRevenueBetween(from, to);
            result.add(MonthlyDataPoint.builder()
                    .month(ym.format(MONTH_FORMATTER))
                    .count(0)
                    .amount(amount)
                    .build());
        }
        return result;
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}