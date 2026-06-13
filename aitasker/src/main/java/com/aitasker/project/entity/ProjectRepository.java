package com.aitasker.project.entity;

import com.aitasker.common.enums.ProjectStatus;
import com.aitasker.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * ProjectRepository — TV3 (Lê Hoàng Anh)
 *
 * Được dùng trong PaymentServiceImpl:
 *   - deposit() → projectRepository.findById(request.getProjectId())
 *     để xác nhận project tồn tại trước khi tạo Payment/Escrow
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Tìm project theo tên (hỗ trợ tìm kiếm)
    List<Project> findByProjectNameContainingIgnoreCase(String name);

    // Tìm project theo trạng thái (ví dụ: lọc ACTIVE cho marketplace)
    List<Project> findByStatus(ProjectStatus status);
}