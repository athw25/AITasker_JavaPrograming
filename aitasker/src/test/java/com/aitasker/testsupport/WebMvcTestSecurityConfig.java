package com.aitasker.testsupport;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Dùng cho @WebMvcTest: giữ nguyên @PreAuthorize (method security) nhưng bỏ
 * JwtFilter/RateLimitFilter thật (vốn cần DB/JwtService) — @WithMockUser hoặc
 * SecurityMockMvcRequestPostProcessors.user(...) sẽ set sẵn Authentication.
 */
@TestConfiguration
@EnableMethodSecurity
public class WebMvcTestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
