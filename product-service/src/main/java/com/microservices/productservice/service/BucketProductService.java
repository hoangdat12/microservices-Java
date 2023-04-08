package com.microservices.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.productservice.model.BucketProduct;
import com.microservices.productservice.model.Product;
import com.microservices.productservice.repository.BucketProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketProductService {
    private final BucketProductRepository repository;
    private final MongoTemplate mongoTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    public boolean insert(Product product, String bucketId) {
        try {
            Pattern _bucketId = Pattern.compile("^" + bucketId + "_");
            // Ensure that the bucket does not exceed 20 products.
            Query query = new Query(Criteria.where("bucketId").regex(_bucketId).and("count").lt(20));
            Update update = new Update()
                    .push("products", product)
                    .inc("count", 1)
                    .setOnInsert("bucketId", bucketId + "_" + new Date().getTime());
            FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
            mongoTemplate.findAndModify(query, update, options, BucketProduct.class);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Product> pagination(String bucketId, Integer page, String sortedBy)
            throws JsonProcessingException
    {
        String _bucket = bucketId + page.toString();
        String regexBucketId = "^" + _bucket + "_";
        Pattern _bucketIdQuery = Pattern.compile("^" + bucketId + "_");
        if (Boolean.TRUE.equals(redisTemplate.hasKey(regexBucketId))) {
            // Return data to the client
            String data = (String) redisTemplate.opsForValue().get(regexBucketId);
            BucketProduct bucketProducts = objectMapper.readValue(
                    data,
                    new TypeReference<BucketProduct>() {}
            );
            return bucketProducts.getProducts();
        }
        else {
            Query query = new Query()
                    .addCriteria(Criteria.where("bucketId").regex(_bucketIdQuery))
                    .with(Sort.by(sortedBy).ascending())
                    .skip(page - 1);
            BucketProduct bucketProducts = mongoTemplate.findOne(query, BucketProduct.class);
            if (bucketProducts != null) {
                int expirationTime = ThreadLocalRandom.current().nextInt(600, 1000);
                redisTemplate.opsForValue().set(
                        regexBucketId,
                        objectMapper.writeValueAsString(bucketProducts),
                        Duration.ofSeconds(expirationTime)
                );
                return bucketProducts.getProducts();
            }
            return null;
        }
    }
}
