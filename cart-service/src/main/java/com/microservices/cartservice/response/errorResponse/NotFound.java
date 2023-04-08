package com.microservices.cartservice.response.errorResponse;

public class NotFound extends RuntimeException{
    public NotFound(String errMessage) {
        super(errMessage);
    }
}
