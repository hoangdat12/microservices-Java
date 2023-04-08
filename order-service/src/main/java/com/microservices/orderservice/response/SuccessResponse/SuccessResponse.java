package com.microservices.orderservice.response.SuccessResponse;

import lombok.Data;

@Data
public class SuccessResponse {
    private String status;
    private Number statusCode;
    private String message;
    private Object metaData;
    public SuccessResponse(Number statusCode, String message, Object metaData) {
        this.status = "Success!";
        this.statusCode = statusCode;
        this.message = message;
        this.metaData = metaData;
    }
    public SuccessResponse(Number statusCode, Object metaData) {
        this.status = "Success!";
        this.statusCode = statusCode;
        this.message = "Success!";
        this.metaData = metaData;
    }
}
