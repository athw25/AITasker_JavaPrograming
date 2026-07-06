package com.aitasker.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.email")
@Getter
@Setter
public class EmailProperties {
    private String from;
    private String fromName;
    private String supportEmail;