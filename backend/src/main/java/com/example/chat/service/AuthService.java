package com.example.chat.service;

import com.example.chat.dto.AuthResponse;
import com.example.chat.dto.LoginRequest;
import com.example.chat.dto.RegisterRequest;
import com.example.chat.dto.UserDto;
import com.example.chat.model.User;
import com.example.chat.repository.UserRepository;
import com.example.chat.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * DESIGN PATTERN - Service layer for authentication business logic
 * TECHNICAL CONCEPTS: DESIGN PATTERN (Service), JWT, STRUCTURE LAYER, RESOLVE SOLUTION
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;  // DESIGN PATTERN - Repository pattern

    @Autowired
    private PasswordEncoder passwordEncoder;  // SESSION/COOKIES - Password encoding

    @Autowired
    private JwtTokenProvider jwtTokenProvider;  // JWT - Token provider

    // JWT - User login with token generation
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));  // RESOLVE SOLUTION - Exception handling

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {  // SESSION/COOKIES - Password validation
            throw new RuntimeException("Invalid password");  // RESOLVE SOLUTION - Exception handling
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());  // JWT - Generate token
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), 
                                    user.getDisplayName(), user.isOnline());  // DESIGN PATTERN - DTO pattern

        return new AuthResponse(token, userDto);  // DESIGN PATTERN - DTO pattern
    }

    // JWT - User registration with token generation
    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {  // DESIGN PATTERN - Repository pattern
            throw new RuntimeException("Username is already taken");  // RESOLVE SOLUTION - Exception handling
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {  // DESIGN PATTERN - Repository pattern
            throw new RuntimeException("Email is already taken");  // RESOLVE SOLUTION - Exception handling
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setDisplayName(registerRequest.getDisplayName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));  // SESSION/COOKIES - Password encoding

        User savedUser = userRepository.save(user);  // DESIGN PATTERN - Repository pattern

        String token = jwtTokenProvider.generateToken(savedUser.getUsername());  // JWT - Generate token
        UserDto userDto = new UserDto(savedUser.getId(), savedUser.getUsername(), 
                                    savedUser.getEmail(), savedUser.getDisplayName(), savedUser.isOnline());  // DESIGN PATTERN - DTO pattern

        return new AuthResponse(token, userDto);  // DESIGN PATTERN - DTO pattern
    }
}
