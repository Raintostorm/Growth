package com.example.chat.exception;

/**
 * RESOLVE SOLUTION - Custom exception for validation failures
 * TECHNICAL CONCEPT: RESOLVE SOLUTION
 */
public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
