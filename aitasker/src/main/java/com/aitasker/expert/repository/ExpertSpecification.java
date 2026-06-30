package com.aitasker.expert.repository;

import com.aitasker.expert.entity.ExpertProfile;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ExpertSpecification {

    public static Specification<ExpertProfile> filterExperts(String skill, Double minRating, Integer minExperience) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo kỹ năng (Tìm kiếm tương đối gần đúng chứa ký tự)
            if (skill != null && !skill.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("skills")), 
                        "%" + skill.toLowerCase() + "%"
                ));
            }

            // 2. Lọc theo số sao đánh giá (Rating lớn hơn hoặc bằng mức yêu cầu)
            if (minRating != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating));
            }

            // 3. Lọc theo số năm kinh nghiệm (Experience lớn hơn hoặc bằng)
            if (minExperience != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("experienceYears"), minExperience));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}