package com.aitasker.expert.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.expert.entity.Attachment;
import com.aitasker.expert.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@Tag(name = "File Storage Module", description = "Hệ thống lưu trữ, upload và download dữ liệu tập tin đính kèm")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Tải tập tin mới lên hệ thống (Portfolio, Delivery, Project Attachments)")
    public ApiResponse<Attachment> uploadFile(@RequestParam("file") MultipartFile file) {
        Long currentUserId = 1L; // Tạm thời giả lập ID người dùng đăng nhập để test
        Attachment attachment = fileStorageService.uploadFile(file, currentUserId);
        return ApiResponse.success("Tải tập tin lên hệ thống thành công!", attachment);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Tải xuống hoặc đọc nội dung tập tin theo ID")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Resource resource = fileStorageService.downloadFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Xóa tập tin đính kèm khỏi hệ thống theo ID")
    public ApiResponse<Void> deleteFile(@PathVariable Long id) {
        Long currentUserId = 1L; // Tạm thời giả lập ID
        fileStorageService.deleteFile(id, currentUserId);
        return ApiResponse.success("Xóa tập tin khỏi hệ thống thành công!", null);
    }
}