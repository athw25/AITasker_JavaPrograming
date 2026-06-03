package com.aitasker.job.entity;
import com.aitasker.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "job_posts")
@Getter
@Setter
public class JobPost extends BaseEntity {
}