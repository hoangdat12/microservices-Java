package com.microservices.orderservice.event;

import com.microservices.orderservice.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class OrderNotifyEvent {
    private List<OrderItem> products;
    private String userEmail;
    private double price;
    public OrderNotifyEvent(List<OrderItem> products, String userEmail, double price) {
        this.products = products;
        this.userEmail = userEmail;
        this.price = price;
    }
}
