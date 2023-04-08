package com.microservices.orderservice.response.SuccessResponse;

public class Ok extends SuccessResponse {
    public Ok(String message, Object metaData) {
        super(200, message, metaData);
    }

    public Ok(Object metaData) {
        super(200, metaData);
    }
}
