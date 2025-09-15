package com.example.chat.service;

import com.example.chat.dto.AuthResponse;
import com.example.chat.dto.LoginRequest;
import com.example.chat.dto.RegisterRequest;
import com.example.chat.dto.UserDto;
import com.example.chat.model.User;
import com.example.chat.repository.UserRepository;
import com.example.chat.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * MOCKITO/JACOCO - Unit tests for AuthService using Mockito
 * TECHNICAL CONCEPT: MOCKITO/JACOCO
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;  // MOCKITO/JACOCO - Mock repository

    @Mock
    private PasswordEncoder passwordEncoder;  // MOCKITO/JACOCO - Mock password encoder

    @Mock
    private JwtTokenProvider jwtTokenProvider;  // MOCKITO/JACOCO - Mock JWT provider

    @InjectMocks
    private AuthService authService;  // MOCKITO/JACOCO - Inject mocks into service

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // MOCKITO/JACOCO - Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setDisplayName("Test User");
        testUser.setPassword("encodedPassword");
        testUser.setOnline(true);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setDisplayName("New User");
        registerRequest.setPassword("password123");
    }

    @Test
    void testLogin_Success() {
        // MOCKITO/JACOCO - Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken("testuser")).thenReturn("jwt-token");

        // MOCKITO/JACOCO - When
        AuthResponse response = authService.login(loginRequest);

        // MOCKITO/JACOCO - Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUser().getUsername());
        
        // MOCKITO/JACOCO - Verify interactions
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verify(jwtTokenProvider).generateToken("testuser");
    }

    @Test
    void testLogin_UserNotFound() {
        // MOCKITO/JACOCO - Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // MOCKITO/JACOCO - When & Then
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        verify(userRepository).findByUsername("testuser");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void testLogin_InvalidPassword() {
        // MOCKITO/JACOCO - Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        // MOCKITO/JACOCO - When & Then
        assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password", "encodedPassword");
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void testRegister_Success() {
        // MOCKITO/JACOCO - Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateToken("testuser")).thenReturn("jwt-token");

        // MOCKITO/JACOCO - When
        AuthResponse response = authService.register(registerRequest);

        // MOCKITO/JACOCO - Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("testuser", response.getUser().getUsername());
        
        // MOCKITO/JACOCO - Verify interactions
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).generateToken("testuser");
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        // MOCKITO/JACOCO - Given
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        // MOCKITO/JACOCO - When & Then
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // MOCKITO/JACOCO - Given
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        // MOCKITO/JACOCO - When & Then
        assertThrows(RuntimeException.class, () -> authService.register(registerRequest));
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtTokenProvider);
    }
}
