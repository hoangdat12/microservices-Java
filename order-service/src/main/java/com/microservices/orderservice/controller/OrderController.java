package com.microservices.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microservices.orderservice.dto.ApplyDiscountCodeDto;
import com.microservices.orderservice.dto.CancelOrderRequest;
import com.microservices.orderservice.dto.OrderRequest;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.response.SuccessResponse.Ok;
import com.microservices.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getOrderByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(orderService.getAllOrderByUserId(userId), HttpStatus.OK);
    }
    @PostMapping("/")
//    @CircuitBreaker(name= "inventory", fallbackMethod = "fallbackMethod")
//    @TimeLimiter(name= "inventory")
//    @Retry(name= "inventory")
    // Error data type return in FallbackMethod
    public ResponseEntity<?> order(@RequestBody OrderRequest request) throws JsonProcessingException {
        return new ResponseEntity<>(orderService.order(request), HttpStatus.CREATED);
    }
    @DeleteMapping("/")
    public ResponseEntity<String> cancelOrder(@RequestBody CancelOrderRequest request) {
        return new ResponseEntity<>(orderService.cancelOrder(request), HttpStatus.OK);
    }
    @PostMapping("/apply-discount")
    public ResponseEntity<?> getPriceWithDiscount(@RequestBody ApplyDiscountCodeDto request) {
        double price = orderService.getPriceWithDiscount(request.getDiscountCode(), request.getPrice());
        Ok response = new Ok("Success", price);
        return ResponseEntity.ok(response);
    }
    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order after some time!");
    }
}
