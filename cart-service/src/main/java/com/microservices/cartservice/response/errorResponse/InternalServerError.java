package com.microservices.cartservice.response.errorResponse;

public class InternalServerError extends RuntimeException{
    public InternalServerError(String errMessage) {
        super(errMessage);
    }
}
