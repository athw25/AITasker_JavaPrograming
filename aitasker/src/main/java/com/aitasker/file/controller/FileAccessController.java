package com.aitasker.file.controller;

import com.aitasker.audit.service.AuditLogService;
import com.aitasker.common.exception.ForbiddenException;
import com.aitasker.common.exception.NotFoundException;
import com.aitasker.common.response.ApiResponse;
import com.aitasker.common.security.SecurityUtils;
import com.aitasker.file.entity.FileEntity;
import com.aitasker.file.repository.FileRepository;
import com.aitasker.file.security.FileValidator;
import com.aitasker.file.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "File management with security")
@Slf4j
public class FileAccessController {

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final FileValidator fileValidator;
    private final AuditLogService auditLogService;
    private final HttpServletRequest httpServletRequest;

    @GetMapping("/{fileId}")
    @Operation(summary = "Download file with access control")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            FileEntity file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new NotFoundException("File không tồn tại"));

            Long currentUserId = SecurityUtils.getCurrentUserId();
            String currentUserEmail = SecurityUtils.getCurrentUserEmail();
            boolean isOwner = file.getOwnerId().equals(currentUserId);
            boolean isAdmin = SecurityUtils.isAdmin();

            if (!isOwner && !isAdmin) {
                String ipAddress = auditLogService.getClientIp(httpServletRequest);
                String userAgent = httpServletRequest.getHeader("User-Agent");
                auditLogService.logFileAccess(currentUserId, currentUserEmail, fileId, "FILE_DOWNLOAD",
                        "Unauthorized access attempt", ipAddress, userAgent, "FAILED");
                throw new ForbiddenException("Không có quyền truy cập file này");
            }

            Resource resource = fileStorageService.load(file.getPath());

            // Log successful download
            String ipAddress = auditLogService.getClientIp(httpServletRequest);
            String userAgent = httpServletRequest.getHeader("User-Agent");
            auditLogService.logFileAccess(currentUserId, currentUserEmail, fileId, "FILE_DOWNLOAD",
                    "File downloaded successfully", ipAddress, userAgent, "SUCCESS");

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error downloading file: {}", fileId, e);
            throw new RuntimeException("Lỗi tải file: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    @Operation(summary = "Upload file with validation")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            String userEmail = SecurityUtils.getCurrentUserEmail();
            String ipAddress = auditLogService.getClientIp(httpServletRequest);
            String userAgent = httpServletRequest.getHeader("User-Agent");

            // Validate file
            fileValidator.validate(file);

            // Save file
            FileEntity savedFile = fileStorageService.save(file, userId);

            // Log successful upload
            auditLogService.logFileAccess(userId, userEmail, savedFile.getId(), "FILE_UPLOAD",
                    "File uploaded: " + file.getOriginalFilename(), ipAddress, userAgent, "SUCCESS");

            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", savedFile.getId()));

        } catch (Exception e) {
            Long userId = SecurityUtils.getCurrentUserId();
            String userEmail = SecurityUtils.getCurrentUserEmail();
            String ipAddress = auditLogService.getClientIp(httpServletRequest);
            String userAgent = httpServletRequest.getHeader("User-Agent");

            auditLogService.logFileAccess(userId, userEmail, null, "FILE_UPLOAD",
                    "File upload failed: " + e.getMessage(), ipAddress, userAgent, "FAILED");

            log.error("File upload error: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.fail("Lỗi tải lên: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file (owner or admin only)")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable Long fileId) {
        try {
            FileEntity file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new NotFoundException("File không tồn tại"));

            Long currentUserId = SecurityUtils.getCurrentUserId();
            String currentUserEmail = SecurityUtils.getCurrentUserEmail();
            boolean isOwner = file.getOwnerId().equals(currentUserId);
            boolean isAdmin = SecurityUtils.isAdmin();

            if (!isOwner && !isAdmin) {
                String ipAddress = auditLogService.getClientIp(httpServletRequest);
                String userAgent = httpServletRequest.getHeader("User-Agent");
                auditLogService.logFileAccess(currentUserId, currentUserEmail, fileId, "FILE_DELETE",
                        "Unauthorized delete attempt", ipAddress, userAgent, "FAILED");
                throw new ForbiddenException("Không có quyền xóa file này");
            }

            fileStorageService.delete(file.getPath());
            fileRepository.deleteById(fileId);

            String ipAddress = auditLogService.getClientIp(httpServletRequest);
            String userAgent = httpServletRequest.getHeader("User-Agent");
            auditLogService.logFileAccess(currentUserId, currentUserEmail, fileId, "FILE_DELETE",
                    "File deleted", ipAddress, userAgent, "SUCCESS");

            return ResponseEntity.ok(ApiResponse.success("File deleted"));

        } catch (Exception e) {
            log.error("Error deleting file: {}", fileId, e);
            return ResponseEntity.ok(ApiResponse.fail("Lỗi xóa file: " + e.getMessage()));
        }
    }
}
