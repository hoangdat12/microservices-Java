package com.microservices.orderservice.dto;

import com.microservices.orderservice.ultils.discount.Condition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformationDiscount {
    private String type;
    private String discountCode;
    private int quantity;
    private double discount;
    private String key;
    private String description;
    private Condition condition;
    private LocalDateTime createdAt;
    private int effectedFrom;
    private int expiresIn;

}
