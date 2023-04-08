package com.microservices.orderservice.response.ErrorResponse;

public class BadRequest extends ErrorResponse{
    public BadRequest(String message, Object metaData) {
        super(400, message, metaData);
    }

    public BadRequest(Object metaData) {
        super(400, metaData);
    }

}
