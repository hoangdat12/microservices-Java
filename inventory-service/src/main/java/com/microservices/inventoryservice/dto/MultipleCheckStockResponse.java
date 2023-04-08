package com.microservices.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultipleCheckStockResponse {
    private boolean isStocks;
    private List<Product> productSaleOut;
}
