package com.aitasker.delivery.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitDeliveryRequest {

    /**
     * Milestone được nộp.
     */
    @NotNull(
            message = "Milestone ID không được để trống."
    )
    private Long milestoneId;

    /**
     * Link file bàn giao.
     *
     * Ví dụ:
     * Google Drive
     * S3 URL
     * Github Repository
     */
    @NotBlank(
            message = "File URL không được để trống."
    )
    @Size(
            max = 1000,
            message = "File URL quá dài."
    )
    private String fileUrl;

    /**
     * Ghi chú khi bàn giao.
     */
    @Size(
            max = 2000,
            message = "Ghi chú tối đa 2000 ký tự."
    )
    private String note;
}