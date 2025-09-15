package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * SESSION/COOKIES - Session management and security configuration
 * TECHNICAL CONCEPTS: SESSION/COOKIES, JWT, OAUTH
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // SESSION/COOKIES - Password encoding for secure storage
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // SESSION/COOKIES - Security filter chain configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // SESSION/COOKIES - CORS for cross-origin requests
            .csrf(csrf -> csrf.disable())  // SESSION/COOKIES - Disable CSRF for stateless JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // SESSION/COOKIES - Stateless session
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // SESSION/COOKIES - Permit all requests (for debugging)
            )
            .headers(headers -> headers.frameOptions().disable());  // SESSION/COOKIES - Allow iframe embedding

        return http.build();
    }

    // SESSION/COOKIES - CORS configuration for cross-origin requests
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));  // SESSION/COOKIES - Allow all origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // SESSION/COOKIES - Allowed HTTP methods
        configuration.setAllowedHeaders(Arrays.asList("*"));  // SESSION/COOKIES - Allowed headers
        configuration.setAllowCredentials(true);  // SESSION/COOKIES - Allow credentials
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
