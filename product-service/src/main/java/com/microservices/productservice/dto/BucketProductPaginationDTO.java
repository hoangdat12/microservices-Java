package com.microservices.productservice.dto;

import com.microservices.productservice.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BucketProductPaginationDTO {
    private Integer page;
    private Integer pageSize;
    private String sortedBy;
    private List<Product> products;
}
