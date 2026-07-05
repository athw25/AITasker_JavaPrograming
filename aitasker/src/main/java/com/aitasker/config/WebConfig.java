package com.aitasker.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    // Rate limiting được xử lý bởi RateLimitFilter thay thế
    // Filter tốt hơn vì nó hoạt động ở servlet-level, không chỉ controller-level
}