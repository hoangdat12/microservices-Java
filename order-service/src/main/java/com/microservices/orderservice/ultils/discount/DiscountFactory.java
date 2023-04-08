package com.microservices.orderservice.ultils.discount;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class DiscountFactory {
    public Discount createDiscount(
            String type,
            String discountCode,
            double discount,
            String key,
            String description,
            LocalDateTime createdAt,
            int effectedFrom,
            int expiresIn,
            Condition condition
    ) {
        switch (type) {
            case "percentage":
                return new PercentageDiscount(
                        discountCode, discount, key, description, createdAt, effectedFrom, expiresIn, condition
                );
            case "amount":
                return new AmountDiscount(
                        discountCode, discount, key, description, createdAt, effectedFrom, expiresIn, condition
                );
            case "coin":
                return new CoinDiscount(
                        discountCode, discount, key, description, createdAt, effectedFrom, expiresIn, condition
                );
            default:
                log.info("Invalid discount type: {}", type);
                throw new IllegalArgumentException("Invalid discount type: " + type);
        }
    }
}
