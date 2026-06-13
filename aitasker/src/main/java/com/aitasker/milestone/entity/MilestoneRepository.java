package com.aitasker.milestone.entity;

import com.aitasker.common.enums.MilestoneStatus;
import com.aitasker.milestone.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * MilestoneRepository — TV1 (Hồ Ngọc Anh Thư)
 *
 * Được dùng trong PaymentServiceImpl:
 *   - deposit()  → tìm Milestone theo ID để gắn vào Payment
 *   - release()  → kiểm tra Milestone.status == APPROVED trước khi giải ngân
 */
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    // Tìm tất cả Milestone theo trạng thái (ví dụ: lọc APPROVED để admin xem)
    List<Milestone> findByStatus(MilestoneStatus status);
}