package com.aitasker.admin.service;

import com.aitasker.common.response.PageResponse;
import com.aitasker.exception.BusinessException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.job.dto.JobPostResponse;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminJobService {
    private final JobPostRepository jobPostRepository;
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public PageResponse<JobPostResponse> getAllJobs(int page, int size){
        Page<JobPost> jobPage = jobPostRepository.findAll(PageRequest.of(page, size));
        return new PageResponse<>(
                jobPage.getContent().stream().map(this::toResponse).toList(),
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages(),
                jobPage.isFirst(),
                jobPage.isLast()
        );
    }

    @Transactional
    public void deleteJob(Long id) {
        JobPost job = jobPostRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Job not found"));
        if (projectRepository.existsByJob_Id(id)) {
            throw new BusinessException("Cannot delete job that has active projects");
        }

        jobPostRepository.deleteById(id);
    }

    private JobPostResponse toResponse(JobPost job){
        JobPostResponse response = new JobPostResponse();

        response.setId(job.getId());
        response.setTitle(job.getTitle());
        response.setDescription(job.getDescription());
        response.setBudget(job.getBudget());
        response.setDeadline(job.getDeadline());
        response.setRequiredSkills(job.getRequiredSkills());
        response.setStatus(job.getStatus());

        if(job.getClient() != null){
            response.setClientId(job.getClient().getId());
            response.setClientName(job.getClient().getName());
        }
        return response;
    }
}
