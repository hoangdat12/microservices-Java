package com.microservices.cartservice.dto;

import com.microservices.productservice.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DetailCartResponse {
    private String id;
    private String userId;
    private List<Product> products;
    private double price;
    private int quantityAllProduct;
}
