package com.aitasker.file.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.file.dto.AttachmentResponse;
import com.aitasker.file.entity.Attachment;
import com.aitasker.file.service.FileStorageService;
import com.aitasker.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AttachmentResponse> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Attachment attachment = fileStorageService.store(file, principal.getUser());
        return ApiResponse.success(AttachmentResponse.from(attachment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Attachment attachment = fileStorageService.getMetadataForDownload(id, principal.getUser());
        Resource resource = fileStorageService.loadAsResource(id);
        MediaType mediaType = attachment.getContentType() != null
                ? MediaType.parseMediaType(attachment.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header("Content-Disposition", "attachment; filename=\"" + attachment.getOriginalFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadWithPermission(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Attachment attachment = fileStorageService.getMetadataForDownload(id, principal.getUser());
        Resource resource = fileStorageService.loadAsResource(id);
        MediaType mediaType = attachment.getContentType() != null
                ? MediaType.parseMediaType(attachment.getContentType())
                : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header("Content-Disposition", "attachment; filename=\"" + attachment.getOriginalFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        fileStorageService.delete(id, principal.getUser());
        return ApiResponse.success("Đã xóa file", null);
    }
}
