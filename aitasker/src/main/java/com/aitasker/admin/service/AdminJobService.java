package com.aitasker.admin.service;

import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminJobService {
    private final JobPostRepository jobPostRepository;
    private final ProjectRepository projectRepository;
    public List<JobPost> getAllJobs(){
        return jobPostRepository.findAll();
    }

    public void deleteJob(Long id) {
        if (projectRepository.existsByJob_Id(id)) {
            throw new RuntimeException("Cannot delete job that has active projects");
        }
        jobPostRepository.deleteById(id);
    }
}
