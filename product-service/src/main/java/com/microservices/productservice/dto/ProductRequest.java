package com.microservices.productservice.dto;

import com.microservices.productservice.model.Specials;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private String code;
    private String name;
    private String author;
    private String description;
    private String image_url;
    private Double price;
    private List<String> type;
    private Specials specs;
}
