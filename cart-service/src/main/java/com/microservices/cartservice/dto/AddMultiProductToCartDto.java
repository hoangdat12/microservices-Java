package com.microservices.cartservice.dto;

import com.microservices.cartservice.model.ProductCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMultiProductToCartDto {
    private String userId;
    private String cartId;
    private List<ProductCart> productCarts;
}
