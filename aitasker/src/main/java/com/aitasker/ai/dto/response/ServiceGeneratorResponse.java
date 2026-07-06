package com.aitasker.ai.dto.response;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceGeneratorResponse {
    private String serviceDescription; // AI sinh bài viết quảng cáo dịch vụ [cite: 216]
    private List<String> tags;         // AI sinh các thẻ từ khóa SEO [cite: 217]
    private String pricingSuggestion;  // AI gợi ý các mức giá bán [cite: 218]
}