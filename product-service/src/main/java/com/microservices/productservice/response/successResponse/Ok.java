package com.microservices.productservice.response.successResponse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Ok<T> extends SuccessResponse<T> {
    public Ok() {
        super(HttpStatus.OK.value(), null);
    }

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
