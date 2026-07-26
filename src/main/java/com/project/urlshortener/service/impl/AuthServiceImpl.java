package com.project.urlshortener.service.impl;

import com.project.urlshortener.constants.ErrorCodes;
import com.project.urlshortener.dto.request.LoginRequest;
import com.project.urlshortener.dto.request.RefreshTokenRequest;
import com.project.urlshortener.dto.request.RegisterRequest;
import com.project.urlshortener.dto.response.AuthResponse;
import com.project.urlshortener.entity.RefreshToken;
import com.project.urlshortener.entity.User;
import com.project.urlshortener.entity.UserRole;
import com.project.urlshortener.exception.BadRequestException;
import com.project.urlshortener.exception.ConflictException;
import com.project.urlshortener.exception.ResourceNotFoundException;
import com.project.urlshortener.mapper.UserMapper;
import com.project.urlshortener.repository.RefreshTokenRepository;
import com.project.urlshortener.repository.UserRepository;
import com.project.urlshortener.security.JwtService;
import com.project.urlshortener.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email is already registered", ErrorCodes.CONFLICT);
        }

        Instant now = Instant.now();
        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);
        return issueTokens(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", ErrorCodes.RESOURCE_NOT_FOUND));
        return issueTokens(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new BadRequestException("Refresh token is invalid", ErrorCodes.BAD_REQUEST);
        }

        String tokenHash = TokenHashUtil.sha256(refreshToken);
        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadRequestException("Refresh token is not recognized", ErrorCodes.BAD_REQUEST));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token has expired or was revoked", ErrorCodes.BAD_REQUEST);
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);
        return issueTokens(storedToken.getUser());
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        persistRefreshToken(user, refreshToken);

        return new AuthResponse(
                UserMapper.toResponse(user),
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    private void persistRefreshToken(User user, String refreshToken) {
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(TokenHashUtil.sha256(refreshToken));
        token.setExpiresAt(jwtService.parseClaims(refreshToken).getExpiration().toInstant());
        token.setRevoked(false);
        token.setCreatedAt(Instant.now());
        refreshTokenRepository.save(token);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
