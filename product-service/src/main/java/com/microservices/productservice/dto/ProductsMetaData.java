package com.microservices.productservice.dto;

import com.microservices.productservice.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductsMetaData {
    private List<Product> products;
}

