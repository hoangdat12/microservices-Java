package com.microservices.orderservice.ultils.discount;

import java.time.LocalDateTime;

public class AmountDiscount extends Discount{
    public AmountDiscount(
            String discountCode,
            double discount,
            String key,
            String description,
            LocalDateTime createdAt,
            int effectedFrom,
            int expiresIn,
            Condition condition
    ) {
        super(discountCode, discount, "amount", key, description, createdAt, effectedFrom, expiresIn, condition);
    }

    @Override
    public double getPrice(double price) {
        return price - this.getDiscount() * 1000;
    }
}
