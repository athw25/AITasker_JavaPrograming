package com.aitasker.admin.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Projects", description = "Admin quản lý dự án")
public class AdminProjectController {

    private final ProjectRepository projectRepository;

    @GetMapping
    @Operation(summary = "Xem tất cả dự án trên hệ thống")
    public ApiResponse<Page<Project>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success("OK", projectRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }
}
