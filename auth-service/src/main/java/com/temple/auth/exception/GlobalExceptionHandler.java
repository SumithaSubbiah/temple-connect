package com.temple.auth.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.temple.auth.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/*
	 * // Handle validation errors
	 * 
	 * @ExceptionHandler(MethodArgumentNotValidException.class) public
	 * ResponseEntity<ErrorResponse>
	 * handleValidation(MethodArgumentNotValidException ex, HttpServletRequest
	 * request) { String message = ex.getBindingResult().getFieldErrors().stream()
	 * .map(err -> err.getField() + ": " + err.getDefaultMessage())
	 * .collect(Collectors.joining(", "));
	 * 
	 * ErrorResponse errorResponse = new ErrorResponse( LocalDateTime.now(),
	 * HttpStatus.BAD_REQUEST.value(), "Validation Error", message,
	 * request.getRequestURI() );
	 * 
	 * return ResponseEntity.badRequest().body(errorResponse); }
	 */

    // Handle custom exceptions
    @ExceptionHandler(EmailAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleEmailConflict(EmailAlreadyRegisteredException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex,  HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

