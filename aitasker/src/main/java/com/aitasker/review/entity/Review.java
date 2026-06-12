package com.aitasker.review.entity;
import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review extends BaseEntity {
    private Integer rating;
    @Column(columnDefinition = "TEXT")
    private String commnet;
    private LocalDateTime createdAt;
}
