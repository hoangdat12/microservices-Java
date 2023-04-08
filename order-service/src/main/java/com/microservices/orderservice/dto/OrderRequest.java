package com.microservices.orderservice.dto;

import com.microservices.orderservice.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private List<OrderItem> products;
    private String discountCode;
    private Long userId;
    private String userEmail;
}
