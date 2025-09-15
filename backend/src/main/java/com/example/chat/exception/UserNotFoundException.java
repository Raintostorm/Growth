package com.example.chat.exception;

/**
 * RESOLVE SOLUTION - Custom exception for user not found scenarios
 * TECHNICAL CONCEPT: RESOLVE SOLUTION
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
