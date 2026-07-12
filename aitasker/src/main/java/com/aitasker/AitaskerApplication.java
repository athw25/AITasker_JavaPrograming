package com.aitasker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AitaskerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AitaskerApplication.class, args);
    }
}
