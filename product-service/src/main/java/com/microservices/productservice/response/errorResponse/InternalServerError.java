package com.microservices.productservice.response.errorResponse;

public class InternalServerError extends RuntimeException{
    public InternalServerError(String errMessage) {
        super(errMessage);
    }
}
