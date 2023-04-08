package com.microservices.cartservice.dto;

import com.microservices.cartservice.model.ProductCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductToCartDto {
    private String userId;
    private String cartId;
    private ProductCart productCart;
}
