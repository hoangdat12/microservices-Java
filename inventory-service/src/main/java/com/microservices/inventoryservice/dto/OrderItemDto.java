package com.microservices.inventoryservice.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long id;
    private String productId;
    private String name;
    private Float price;
    private String author;
    private Integer quantity;
}
