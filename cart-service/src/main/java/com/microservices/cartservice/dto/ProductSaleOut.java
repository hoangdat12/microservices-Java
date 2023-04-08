package com.microservices.cartservice.dto;

import com.microservices.cartservice.model.ProductCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSaleOut {
    private String message;
    private List<ProductCart> productCart;
}
