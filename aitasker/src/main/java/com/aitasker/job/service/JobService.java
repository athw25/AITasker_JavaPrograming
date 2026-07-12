package com.aitasker.job.service;

import com.aitasker.analytics.enums.AnalyticsEventType;
import com.aitasker.analytics.service.AnalyticsService;
import com.aitasker.common.enums.JobStatus;
import com.aitasker.common.enums.Role;
import com.aitasker.common.response.PageResponse;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.job.dto.JobPostRequest;
import com.aitasker.job.dto.JobPostResponse;
import com.aitasker.job.dto.JobSearchRequest;
import com.aitasker.job.entity.JobPost;
import com.aitasker.job.repository.JobPostRepository;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final AnalyticsService analyticsService;

    public JobPostResponse create(JobPostRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        JobPost job = new JobPost();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setDeadline(request.getDeadline());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setClient(client);
        JobPost saved = jobPostRepository.save(job);
        analyticsService.recordEvent(AnalyticsEventType.JOB_CREATED, client.getId(), Role.CLIENT.name(),
                "JOB", String.valueOf(saved.getId()));
        return toResponse(saved);
    }

    public PageResponse<JobPostResponse> getAll(int page, int size){
        Page<JobPost> jobPage = jobPostRepository.findAll(PageRequest.of(page, size));
        List<JobPostResponse> content = jobPage.getContent().stream().map(this::toResponse).toList();
        return new PageResponse<>(
                content,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages(),
                jobPage.isFirst(),
                jobPage.isLast()
        );
    }

    public List<JobPostResponse> getMyJobs() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User client = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        return jobPostRepository.findByClientId(client.getId()).stream()
                .map(this::toResponse)
                .toList();
    }
    public JobPostResponse getById(Long id){
        return toResponse(findById(id));
    }
    public JobPostResponse update(Long id, JobPostRequest request){
        JobPost job = findById(id);
        checkOwnership(job);
        if(job.getStatus() == JobStatus.IN_PROGRESS){
            throw new com.aitasker.exception.BadRequestException("Không thể cập nhật Job đang IN_PROGRESS");
        }
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setBudget(request.getBudget());
        job.setDeadline(request.getDeadline());
        job.setRequiredSkills(request.getRequiredSkills());

        return toResponse(jobPostRepository.save(job));
    }
    public void delete(long id){
        JobPost job = findById(id);
        checkOwnership(job);
        jobPostRepository.delete(job);
    }

    /**
     * Chỉ chủ sở hữu (client đã tạo job) hoặc ADMIN mới được sửa/xóa job này.
     * Trước bản vá này, mọi CLIENT đã đăng nhập đều sửa/xóa được job của
     * client khác (lỗi IDOR) vì @PreAuthorize("hasRole('CLIENT')") chỉ kiểm
     * tra vai trò chứ không kiểm tra quyền sở hữu.
     */
    private void checkOwnership(JobPost job){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (job.getClient() == null || !job.getClient().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Bạn không có quyền thao tác trên Job này.");
        }
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
        return jobPostRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Job với id: " + id));
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
