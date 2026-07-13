package com.aitasker.admin.service;

import com.aitasker.common.response.PageResponse;
import com.aitasker.project.dto.response.ProjectResponse;
import com.aitasker.project.entity.Project;
import com.aitasker.project.mapper.ProjectMapper;
import com.aitasker.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> getAllProjects(int page, int size) {
        Page<Project> projectPage = projectRepository.findAll(PageRequest.of(page, size));
        return new PageResponse<>(
                projectPage.getContent().stream().map(projectMapper::toResponse).toList(),
                projectPage.getNumber(),
                projectPage.getSize(),
                projectPage.getTotalElements(),
                projectPage.getTotalPages(),
                projectPage.isFirst(),
                projectPage.isLast()
        );
    }
}
