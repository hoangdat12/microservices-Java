package com.microservices.productservice.response.successResponse;

import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
public abstract class SuccessResponse<T> {
    private Number code;
    private String status;
    private T metaData;

    public SuccessResponse(Number code, T metaData) {
        this.code = code;
        this.status = "Success";
        this.metaData = metaData;
    }
    public abstract ResponseEntity<?> sender();

}
