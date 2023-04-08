package com.microservices.orderservice.response.ErrorResponse;

import lombok.Data;

@Data
public class ErrorResponse {
    private String status;
    private Number statusCode;
    private String message;
    private Object metaData;
    public ErrorResponse(Number statusCode, String message, Object metaData) {
        this.status = "Error!";
        this.statusCode = statusCode;
        this.message = message;
        this.metaData = metaData;
    }
    public ErrorResponse(Number statusCode, Object metaData) {
        this.status = "Error!";
        this.statusCode = statusCode;
        this.message = "Internal Error Server!";
        this.metaData = metaData;
    }
}
