package com.aitasker.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin access success");
    }
}