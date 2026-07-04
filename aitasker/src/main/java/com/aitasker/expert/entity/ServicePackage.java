package com.aitasker.expert.entity;

import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Entity
@Table(name = "service_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePackage extends BaseEntity {

    @Column(nullable = false)
    private Long expertId;

    @Column(nullable = false)
    private String packageName;

    // double -> BigDecimal: tránh sai số dấu phẩy động khi tính tiền.
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int deliveryDays;

}
