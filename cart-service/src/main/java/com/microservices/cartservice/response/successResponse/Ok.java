package com.microservices.cartservice.response.successResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Ok<T> extends SuccessResponse<T> {
    public Ok(T metaData) {
        super(HttpStatus.OK.value(), metaData);
    }

    @Override
    public ResponseEntity<?> sender() {
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(this);
    }
}
