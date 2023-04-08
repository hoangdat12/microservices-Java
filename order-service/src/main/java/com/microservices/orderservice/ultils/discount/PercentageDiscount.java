package com.microservices.orderservice.ultils.discount;

import java.time.LocalDateTime;

public class PercentageDiscount extends Discount{
    public PercentageDiscount(
            String discountCode,
            double discount,
            String key,
            String description,
            LocalDateTime createdAt,
            int effectedFrom,
            int expiresIn,
            Condition condition
    ) {
        super(discountCode, discount, "percentage", key, description, createdAt, effectedFrom, expiresIn, condition);
    }

    @Override
    public double getPrice(double price) {
        return price * this.getDiscount();
    }
}
