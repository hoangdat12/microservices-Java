package com.microservices.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyDiscountCodeDto {
    private String discountCode;
    private Long userId;
    private double price;
}
