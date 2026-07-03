package com.aitasker.project.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.project.dto.request.CreateProjectRequest;
import com.aitasker.project.dto.request.UpdateProjectRequest;
import com.aitasker.project.dto.response.ProjectDetailResponse;
import com.aitasker.project.dto.response.ProjectResponse;
import com.aitasker.project.service.ProjectService;
import com.aitasker.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project lifecycle APIs")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Create a project from an accepted proposal")
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Project created successfully",
                projectService.createProject(request, principal.getUser()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project details")
    public ApiResponse<ProjectDetailResponse> get(@PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Project retrieved successfully",
                projectService.getProject(id, principal.getUser()));
    }

    @GetMapping("/me")
    @Operation(summary = "Get every project involving the current user")
    public ApiResponse<List<ProjectResponse>> me(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Projects retrieved successfully",
                projectService.getMyProjects(principal.getUser()));
    }

    @GetMapping("/client")
    @PreAuthorize("hasRole('CLIENT')")
    @Operation(summary = "Get projects owned by the current client")
    public ApiResponse<List<ProjectResponse>> client(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Client projects retrieved successfully",
                projectService.getClientProjects(principal.getUser()));
    }

    @GetMapping("/expert")
    @PreAuthorize("hasRole('EXPERT')")
    @Operation(summary = "Get projects assigned to the current expert")
    public ApiResponse<List<ProjectResponse>> expert(@AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Expert projects retrieved successfully",
                projectService.getExpertProjects(principal.getUser()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Administratively update a project")
    public ApiResponse<ProjectResponse> update(@PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        return ApiResponse.success("Project updated successfully",
                projectService.updateProject(id, request, principal.getUser()));
    }
}
