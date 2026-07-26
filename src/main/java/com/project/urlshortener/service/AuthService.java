package com.project.urlshortener.service;

import com.project.urlshortener.dto.request.LoginRequest;
import com.project.urlshortener.dto.request.RefreshTokenRequest;
import com.project.urlshortener.dto.request.RegisterRequest;
import com.project.urlshortener.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);
}
