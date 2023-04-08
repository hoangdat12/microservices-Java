package com.microservices.productservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.productservice.dto.ProductIds;
import com.microservices.productservice.dto.ProductRequest;
import com.microservices.productservice.service.ProductService;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable String id,
            @RequestBody ProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable String id
    ) {
        return productService.deleteProduct(id);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(
            @PathVariable String id
    ) throws JsonProcessingException {
        return productService.getProduct(id);
    }
    @GetMapping("")
    public ResponseEntity<?> getAllProduct() {
        return productService.getAllProduct();
    }
    @PostMapping("/productIds")
    public ResponseEntity<?> getAllProductInProductIds(@RequestBody ProductIds productIds) {
        return productService.getAllProductInProductIds(productIds.getProductIds());
    }
    @PostMapping("/trend")
    public ResponseEntity<?> getTrendProduct(
            @QueryParam("page") Integer page,
            @QueryParam("limit") Integer limit
    ) {
        limit = limit == null ? 10 : limit;
        page = page == null ? 1 : page;
        return productService.getTrendProduct(limit, page);
    }
}
