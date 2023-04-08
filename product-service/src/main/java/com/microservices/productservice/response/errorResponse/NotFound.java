package com.microservices.productservice.response.errorResponse;

public class NotFound extends RuntimeException{
    public NotFound(String errMessage) {
        super(errMessage);
    }
}
