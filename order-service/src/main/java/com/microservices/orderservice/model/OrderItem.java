package com.microservices.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name= "order-items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productId;
    private String name;
    private Float price;
    private String author;
    private Number quantity;

    public OrderItem(String productId, String name, Float price, String author, Number quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.author = author;
        this.quantity = quantity;
    }
}
