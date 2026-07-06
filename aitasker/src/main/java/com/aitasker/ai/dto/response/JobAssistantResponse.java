package com.aitasker.ai.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobAssistantResponse {
    private String title;            // AI sinh Tiêu đề công việc [cite: 208]
    private String description;      // AI sinh Mô tả chi tiết [cite: 209]
    private List<String> skills;     // AI gợi ý danh sách Kỹ năng [cite: 210]
    private String budgetSuggestion; // AI gợi ý ngân sách [cite: 211]
}