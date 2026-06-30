package com.aitasker.project.mapper;

import com.aitasker.milestone.entity.Milestone;
import com.aitasker.project.dto.response.ProjectDetailResponse;
import com.aitasker.project.dto.response.ProjectResponse;
import com.aitasker.project.entity.Project;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {

        if (project == null) {
            return null;
        }

        int totalMilestones = 0;
        int completedMilestones = 0;

        if (project.getMilestones() != null) {

            totalMilestones = project.getMilestones().size();

            completedMilestones =
                    (int) project.getMilestones()
                            .stream()
                            .filter(
                                    milestone ->
                                            milestone.getStatus() != null
                                                    && milestone.getStatus().name()
                                                    .equals("PAID")
                            )
                            .count();
        }

        return ProjectResponse.builder()
                .id(project.getId())

                .clientId(project.getClient().getId())
                .clientName(project.getClient().getName())

                .expertId(project.getExpert().getId())
                .expertName(project.getExpert().getName())

                .jobId(project.getJob().getId())
                .jobTitle(project.getJob().getTitle())

                .proposalId(project.getProposal().getId())

                .status(project.getStatus())

                .startDate(project.getStartDate())
                .endDate(project.getEndDate())

                .totalMilestones(totalMilestones)
                .completedMilestones(completedMilestones)

                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())

                .build();
    }

    public ProjectDetailResponse toDetailResponse(Project project) {

        if (project == null) {
            return null;
        }

        int totalMilestones = 0;
        int completedMilestones = 0;

        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;

        if (project.getMilestones() != null) {

            totalMilestones = project.getMilestones().size();

            for (Milestone milestone : project.getMilestones()) {

                if (milestone.getAmount() != null) {
                    totalBudget =
                            totalBudget.add(milestone.getAmount());
                }

                if (milestone.getStatus() != null
                        && milestone.getStatus().name().equals("PAID")) {

                    completedMilestones++;

                    if (milestone.getAmount() != null) {
                        paidAmount =
                                paidAmount.add(
                                        milestone.getAmount()
                                );
                    }
                }
            }
        }

        BigDecimal remainingAmount =
                totalBudget.subtract(paidAmount);

        return ProjectDetailResponse.builder()

                .id(project.getId())

                .clientId(project.getClient().getId())
                .clientName(project.getClient().getName())
                .clientEmail(project.getClient().getEmail())

                .expertId(project.getExpert().getId())
                .expertName(project.getExpert().getName())
                .expertEmail(project.getExpert().getEmail())

                .jobId(project.getJob().getId())
                .jobTitle(project.getJob().getTitle())

                .proposalId(project.getProposal().getId())

                .status(project.getStatus())

                .startDate(project.getStartDate())
                .endDate(project.getEndDate())

                .totalMilestones(totalMilestones)
                .completedMilestones(completedMilestones)

                .totalBudget(totalBudget)
                .paidAmount(paidAmount)
                .remainingAmount(remainingAmount)

                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())

                .build();
    }
}