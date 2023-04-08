package com.microservices.cartservice.controller;

import com.microservices.cartservice.dto.*;
import com.microservices.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@Slf4j
public class CartController {
    private final CartService cartService;
    @PostMapping("/{userId}")
    public ResponseEntity<?> createCart(@PathVariable String userId) {
        return cartService.createCart(userId);
    }
    @DeleteMapping
    public ResponseEntity<?> deleteCart(@RequestBody CartRequest request) {
        return cartService.deleteCart(request);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addProductToCart(@RequestBody AddProductToCartDto request) {
        log.info("Run::::::::");
        return cartService.addProductToCart(request);
    }
    @PostMapping("/adds")
    public ResponseEntity<?> addProductToCart(@RequestBody AddMultiProductToCartDto request) {
        return cartService.addMultiProductToCart(request);
    }
    @PostMapping("/delete")
    public ResponseEntity<?> deleteProductToCart(@RequestBody DeleteProductToCart request) {
        return cartService.removeProductToCart(request);
    }
    @PostMapping("/deletes")
    public ResponseEntity<?> deleteMultipleProductToCart(@RequestBody DeleteMultipleProductToCart request) {
        return cartService.removeMultipleProductToCart(request);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getDetailCartOfUser(@PathVariable String userId) throws IOException {
        return cartService.getDetailCartOfUserV2(userId);
    }
}
