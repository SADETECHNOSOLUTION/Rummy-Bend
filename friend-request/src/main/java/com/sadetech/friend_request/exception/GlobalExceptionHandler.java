package com.sadetech.friend_request.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorHandler> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){
        ErrorHandler errorResponse = new ErrorHandler(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(RequestNotFoundException.class)
    public ResponseEntity<ErrorHandler> handleRequestNotFoundException(RequestNotFoundException ex, WebRequest request){
        ErrorHandler errorResponse = new ErrorHandler(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UnAuthorizedAccessException.class)
    public ResponseEntity<ErrorHandler> handleUnAuthorizedAccessException(UnAuthorizedAccessException ex, WebRequest request){
        ErrorHandler errorHandler = new ErrorHandler(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorHandler);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorHandler> handleException(Exception ex, WebRequest request){
        ErrorHandler errorResponse = new ErrorHandler(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
