package com.aitasker.project.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProjectRequest {

    /**
     * Proposal đã được ACCEPT.
     */
    @NotNull(message = "Proposal ID không được để trống.")
    private Long proposalId;

    /**
     * Ngày bắt đầu dự án.
     */
    @FutureOrPresent(
            message = "Ngày bắt đầu phải từ hiện tại trở đi."
    )
    private LocalDate startDate;

    /**
     * Ngày kết thúc dự kiến.
     */
    private LocalDate endDate;
}