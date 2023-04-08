package com.microservices.orderservice.dto;

import com.microservices.orderservice.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponse {
    private boolean isStocks;
    private List<OrderItem> productSaleOut;
}
