package com.aitasker.job.service;

import com.aitasker.common.enums.JobStatus;
import com.aitasker.job.dto.JobPostRequest;
import com.aitasker.job.dto.JobPostResponse;
import com.aitasker.job.dto.JobSearchRequest;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService{
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;

    public JobPostResponse create(JobPostRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        JobPost job = new JobPost();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setDeadline(request.getDeadline());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setStatus(JobStatus.OPEN);
        job.setClient(client);
        return toResponse(jobPostRepository.save(job));
    }
    public List<JobPostResponse> getAll(){
        return jobPostRepository.findAll().stream().map(this::toResponse).toList();
    }
    public JobPostResponse getById(Long id){
        return toResponse(findById(id));
    }
    public JobPostResponse update(Long id, JobPostRequest request){
        JobPost job = findById(id);
        if(job.getStatus() == JobStatus.IN_PROGRESS){
            throw new RuntimeException("Cannnot update a job that in IN_PROGRESS");
        }
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setDeadline(request.getDeadline());
        job.setRequiredSkills(request.getRequiredSkills());

        return toResponse(jobPostRepository.save(job));
    }
    public void delete(long id){
        jobPostRepository.deleteById(id);
    }
    public List<JobPostResponse> search(JobSearchRequest request){
        return jobPostRepository.search(
                request.getKeyword(),
                request.getSkills(),
                request.getMinBudget(),
                request.getMaxBudget(),
                request.getStatus()
        ).stream().map(this::toResponse).toList();
    }
    private JobPost findById(long id){
        return jobPostRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }
    private JobPostResponse toResponse(JobPost job){
        JobPostResponse res = new JobPostResponse();
        res.setId(job.getId());
        res.setTitle(job.getTitle());
        res.setDescription(job.getDescription());
        res.setBudget(job.getBudget());
        res.setDeadline(job.getDeadline());
        res.setRequiredSkills(job.getRequiredSkills());
        res.setStatus(job.getStatus());
        if(job.getClient() != null){
            res.setClientId(job.getClient().getId());
            res.setClientName(job.getClient().getName());
        }
        return res;
    }


}
