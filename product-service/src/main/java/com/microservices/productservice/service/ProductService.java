package com.microservices.productservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.productservice.dto.BucketProductPaginationDTO;
import com.microservices.productservice.dto.ListProductResponse;
import com.microservices.productservice.dto.ProductRequest;
import com.microservices.productservice.model.Product;
import com.microservices.productservice.repository.ProductRepository;
import com.microservices.productservice.response.errorResponse.InternalServerError;
import com.microservices.productservice.response.successResponse.Created;
import com.microservices.productservice.response.successResponse.Ok;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;
    public ResponseEntity<?> createProduct(ProductRequest request) {
        Product newProduct = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .author(request.getAuthor())
                .description(request.getDescription())
                .image_url(request.getImage_url())
                .price(request.getPrice())
                .type(request.getType())
                .active(true)
                .purchases(0)
                .specs(request.getSpecs())
                .build();
        Product productCreated = productRepository.save(newProduct);
        Created<Product> response = new Created<>(productCreated);
        return response.sender();
    }
    public ResponseEntity<?> updateProduct(String productId, ProductRequest request) {
        Query query = new Query(Criteria.where("_id").is(productId));
        Update update = new Update()
                .set("code", request.getCode())
                .set("name", request.getName())
                .set("author", request.getAuthor())
                .set("description", request.getDescription())
                .set("image_url", request.getImage_url())
                .set("price", request.getPrice())
                .set("specs", request.getSpecs());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Product.class);
        Ok<UpdateResult> response = new Ok<>(result);
        return response.sender();
    }
    public ResponseEntity<?> deleteProduct(String productId) {
        Query query = new Query(Criteria.where("_id").is(productId));
        mongoTemplate.remove(query, Product.class);
        Ok<String> response = new Ok<>("Deleted!");
        return response.sender();
    }
    public ResponseEntity<?> getProduct(String productId) throws JsonProcessingException {
        Product product;
        if (redisTemplate.hasKey(productId)) {
            String data = (String) redisTemplate.opsForValue().get(productId);
            product = objectMapper.readValue(data, Product.class);
            log.info("Catch:::: {}", product);
        } else {
            log.info("Catch empty:::: {}");
            Query query = new Query(Criteria.where("_id").is(productId));
            product = mongoTemplate.findOne(query, Product.class);
            if (product == null) {
                Ok<String> response = new Ok<>("Product not found!");
                return response.sender();
            }
            redisTemplate.opsForValue().set(
                    productId,
                    objectMapper.writeValueAsString(product),
                    Duration.ofSeconds(15)
            );
        }
        Ok<Product> response = new Ok<>(product);
        return response.sender();
    }
    public ResponseEntity<?> getAllProduct() {
        List<Product> products = mongoTemplate.findAll(Product.class);
        Ok<List<Product>> response = new Ok<>(products);
        return response.sender();
    }
    public ResponseEntity<?> getAllProductInProductIds(List<String> productIds) {
        Query query = new Query(Criteria.where("_id").in(productIds));
        List<Product> products = mongoTemplate.find(query, Product.class);
        Ok<ListProductResponse> response = new Ok<>(new ListProductResponse(products));
        log.info(response.toString());
        return response.sender();
    }
    public ResponseEntity<?> getTrendProduct(Integer limit, Integer page) {
        int offset = (page - 1) * limit;
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, "purchases"));
        query.limit(limit);
        query.skip(offset);
        List<Product> products = mongoTemplate.find(query, Product.class);
        if (products == null) {
            throw new InternalServerError("DB Error!");
        }
        BucketProductPaginationDTO pagination = BucketProductPaginationDTO.builder()
                .page(page)
                .pageSize(products.size())
                .products(products)
                .build();
        return new Ok<>(pagination).sender();
    }
}

