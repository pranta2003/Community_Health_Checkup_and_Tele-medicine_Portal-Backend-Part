package com.Community.demo.exception;

/**
 * Custom exception for handling bad requests (HTTP 400).
 *
 * This exception can be thrown when:
 *  - Input validation fails
 *  - Required parameters are missing
 *  - Invalid data format or constraints are violated
 *
 * It is handled globally by GlobalExceptionHandler.
 */
public class BadRequestException extends RuntimeException {

    /**
     * Default constructor with message.
     *
     * @param message the detailed error message
     */
    public BadRequestException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message the detailed error message
     * @param cause the underlying cause (optional)
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
