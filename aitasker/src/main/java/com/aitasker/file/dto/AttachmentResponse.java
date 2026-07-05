package com.aitasker.file.dto;

import com.aitasker.file.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {
    private Long id;
    private String originalFileName;
    private String contentType;
    private long fileSize;
    private String downloadUrl;

    public static AttachmentResponse from(Attachment a) {
        return new AttachmentResponse(
                a.getId(),
                a.getOriginalFileName(),
                a.getContentType(),
                a.getFileSize(),
                "/api/files/" + a.getId()
        );
    }
}
