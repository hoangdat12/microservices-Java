package com.microservices.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMultipleProductToCart {
    private String userId;
    private String cartId;
    private List<String> productIds;
}
