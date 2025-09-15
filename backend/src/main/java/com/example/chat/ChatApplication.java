package com.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot Application class for Real-time Chat Application
 * 
 * TECHNICAL CONCEPTS IMPLEMENTED:
 * - JWT Authentication and Authorization
 * - Real-time messaging with WebSocket
 * - RESTful API design
 * - Spring Security integration
 * - Database operations with JPA
 * - Modern Spring Boot practices
 */
@SpringBootApplication
@EnableCaching  // REDIS - Enable caching support
@EnableAsync    // ASYNC - Enable asynchronous processing
public class ChatApplication {

    public ChatApplication() {
        // DESIGN PATTERN - Singleton pattern (Spring managed bean)
    }

    public static void main(final String[] args) {
        // MAVEN - Application entry point
        SpringApplication.run(ChatApplication.class, args);
    }
}
