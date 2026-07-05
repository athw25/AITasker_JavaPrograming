package com.aitasker.expert.repository;

import com.aitasker.common.enums.Role;
import com.aitasker.expert.entity.ExpertProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpertProfileRepository extends JpaRepository<ExpertProfile, Long> {
    Optional<ExpertProfile> findByUserId(Long userId);
    Optional<ExpertProfile> findByUserEmail(String email);

    // Truy vấn tối ưu lấy danh sách Chuyên gia cùng thông tin User đi kèm (Tránh N+1)
    @Query("SELECT ep FROM ExpertProfile ep JOIN FETCH ep.user u WHERE u.role = :role")
    List<ExpertProfile> findByRoleWithUser(@Param("role") Role role);
}
