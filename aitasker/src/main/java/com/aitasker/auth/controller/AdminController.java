package com.aitasker.auth.controller;

import com.aitasker.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/test")
    public ApiResponse<String> test() {
        return ApiResponse.success("Admin access success");
    }
}
