package com.aitasker.project.service;

import com.aitasker.project.dto.request.CreateProjectRequest;
import com.aitasker.project.dto.request.UpdateProjectRequest;
import com.aitasker.project.dto.response.ProjectDetailResponse;
import com.aitasker.project.dto.response.ProjectResponse;
import com.aitasker.user.entity.User;

import java.util.List;

/** Project lifecycle use cases. */
public interface ProjectService {
    ProjectResponse createProject(CreateProjectRequest request, User currentUser);
    ProjectDetailResponse getProject(Long id, User currentUser);
    List<ProjectResponse> getMyProjects(User currentUser);
    List<ProjectResponse> getClientProjects(User currentUser);
    List<ProjectResponse> getExpertProjects(User currentUser);
    ProjectResponse updateProject(Long id, UpdateProjectRequest request, User currentUser);
}
