// Temporary stub implementation for AuthService
package com.aitasker.auth.service;

import org.springframework.stereotype.Service;

import com.aitasker.auth.dto.AuthResponse;
import com.aitasker.auth.dto.LoginRequest;
import com.aitasker.auth.dto.RegisterRequest;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthResponse register(RegisterRequest request) {
        throw new UnsupportedOperationException(
                "Temporary stub: register() has not been implemented yet.");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        throw new UnsupportedOperationException(
                "Temporary stub: login() has not been implemented yet.");
    }
}