package com.project.urlshortener.service.impl;

import com.project.urlshortener.dto.request.LoginRequest;
import com.project.urlshortener.dto.request.RegisterRequest;
import com.project.urlshortener.entity.User;
import com.project.urlshortener.entity.UserRole;
import com.project.urlshortener.repository.RefreshTokenRepository;
import com.project.urlshortener.repository.UserRepository;
import com.project.urlshortener.security.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerShouldCreateUserAndIssueTokens() {
        RegisterRequest request = new RegisterRequest("user@example.com", "Password1");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);
        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(java.util.Date.from(Instant.now().plusSeconds(1200)));
        when(jwtService.parseClaims("refresh-token")).thenReturn(claims);

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void loginShouldDelegateToAuthenticationManager() {
        LoginRequest request = new LoginRequest("user@example.com", "Password1");
        when(jwtService.generateAccessToken(any(User.class))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser()));
        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(java.util.Date.from(Instant.now().plusSeconds(1200)));
        when(jwtService.parseClaims("refresh-token")).thenReturn(claims);

        authService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("user@example.com", captor.getValue().getPrincipal());
    }

    @Test
    void registerShouldRejectDuplicateEmail() {
        when(userRepository.existsByEmail(eq("user@example.com"))).thenReturn(true);

        try {
            authService.register(new RegisterRequest("user@example.com", "Password1"));
        } catch (Exception exception) {
            assertNotNull(exception);
        }

        verify(userRepository, never()).save(any(User.class));
    }

    private User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("encoded");
        user.setRole(UserRole.USER);
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        return user;
    }
}
