package com.example.chat.exception;

/**
 * RESOLVE SOLUTION - Custom exception for authentication failures
 * TECHNICAL CONCEPT: RESOLVE SOLUTION
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
