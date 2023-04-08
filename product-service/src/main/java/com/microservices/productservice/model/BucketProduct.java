package com.microservices.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "bucket_product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BucketProduct {
    @Id
    private String id;
    private String bucketId;
    private int count;
    private List<Product> products;
}
