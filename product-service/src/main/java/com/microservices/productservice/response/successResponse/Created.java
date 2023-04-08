package com.microservices.productservice.response.successResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Created<T> extends SuccessResponse<T> {
    public Created(T metaData) {
        super(HttpStatus.CREATED.value(), metaData);
    }

    @Override
    public ResponseEntity<?> sender() {
        return ResponseEntity
                .status(HttpStatus.CREATED.value())
                .body(this);
    }
}
