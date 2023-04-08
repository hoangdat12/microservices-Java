package com.microservices.orderservice.response.SuccessResponse;

public class Created extends SuccessResponse {
    public Created(String message, Object metaData) {
        super(201, message, metaData);
    }

    public Created(Object metaData) {
        super(201, metaData);
    }
}
