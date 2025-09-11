package com.lumenlabs.energymanagement.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDefault> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
    	ErrorDefault error = new ErrorDefault(
                LocalDateTime.now(),
                ex.getStatusCode().value(),
                ex.getStatusCode().toString(),
                ex.getReason(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDefault> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            sb.append(error.getField())
              .append(": ")
              .append(error.getDefaultMessage())
              .append("; ");
        }

        ErrorDefault apiError = new ErrorDefault(
                LocalDateTime.now(),
                400,
                "Bad Request",
                sb.toString(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDefault> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorDefault error = new ErrorDefault(
            LocalDateTime.now(),
            500,
            "Internal Server Error",
            "Unexcpected Error",
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
}
