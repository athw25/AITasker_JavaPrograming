package com.aitasker.project.dto.request;

import com.aitasker.common.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** Administrative project update payload. */
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Schema(description = "Update project dates or lifecycle status")
public class UpdateProjectRequest {
    private LocalDate startDate;

    @Future
    private LocalDate endDate;

    private ProjectStatus status;
}
