package com.microservices.productservice.repository;

import com.microservices.productservice.model.BucketProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BucketProductRepository extends MongoRepository<BucketProduct, String> {
    Slice<BucketProduct> findByBucketIdStartingWith(String bucketIdPrefix, Pageable pageable);
}
