package com.aitasker.expert.entity;

import com.aitasker.common.entity.BaseEntity;
import com.aitasker.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Thực thể lưu trữ các dự án, sản phẩm nổi bật (Portfolio) của Chuyên gia.
 */
@Entity
@Table(name = "portfolios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Portfolio extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "expert_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_portfolio_expert")
    )
    private User expert;

    @Column(nullable = false)
    private String projectName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String projectUrl;
}