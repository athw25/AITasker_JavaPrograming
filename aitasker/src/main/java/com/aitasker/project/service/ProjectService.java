// ProjectService.java
package com.aitasker.project.service;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.project.entity.Project;
import com.aitasker.project.repository.ProjectRepository;
import com.aitasker.proposal.entity.Proposal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public void createProjectFromProposal(Proposal proposal) {
        Project project = new Project();

        // 1. Lấy thông tin cơ bản từ Job
        project.setProjectName(proposal.getJob().getTitle());
        project.setDescription(proposal.getJob().getDescription());

        // 2. Ép kiểu dữ liệu Giá thầu từ Double (Proposal) sang Integer (Project)
        if (proposal.getBidAmount() != null) {
            project.setTotalBudget(proposal.getBidAmount().intValue());
        }

        // 3. Phân bổ Client và Expert
        project.setClient(proposal.getJob().getClient());
        project.setExpert(proposal.getExpert());

        // 4. Thiết lập thời gian (Ngày bắt đầu là hôm nay)
        project.setStartDate(LocalDate.now());

        // Lưu ý: Nếu JobPost của bạn có trường deadline (LocalDate), bạn có thể set nó cho endDate.
        // Tạm thời ở đây chưa map endDate, hoặc bạn có thể gọi: project.setEndDate(proposal.getJob().getDeadline());

        // 5. Thiết lập trạng thái dự án
        // Chú ý: Đảm bảo file ProjectStatus.java của bạn có giá trị IN_PROGRESS nhé.
        project.setStatus(ProjectStatus.IN_PROGRESS);

        // 6. Lưu xuống Database
        projectRepository.save(project);

        System.out.println("🚀 [SUCCESS] Đã tạo và lưu Project thật vào Database từ Proposal ID: " + proposal.getId());
    }
}