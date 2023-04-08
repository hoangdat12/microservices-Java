package com.microservices.productservice.handler;


import com.microservices.productservice.dto.MessageException;
import com.microservices.productservice.response.errorResponse.InternalServerError;
import com.microservices.productservice.response.errorResponse.NotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleExceptionIllegalArgumentException(IllegalArgumentException exception) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .status("Error")
                .metadata(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(message);
    }
    @ExceptionHandler(NotFound.class)
    public ResponseEntity<?> handleExceptionNotFound(NotFound exception) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .status("Error")
                .metadata(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .body(message);
    }
    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<?> handleExceptionInternalServerError(InternalServerError exception) {
        MessageException<Object> message = MessageException.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status("Error")
                .metadata(exception.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(message);
    }
}
