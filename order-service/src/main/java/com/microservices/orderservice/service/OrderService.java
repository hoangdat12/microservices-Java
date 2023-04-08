package com.microservices.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.orderservice.repository.OrderRepository;
import com.microservices.orderservice.response.ErrorResponse.BadRequest;
import com.microservices.orderservice.dto.*;
import com.microservices.orderservice.event.OrderNotifyEvent;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.model.OrderItem;
import com.microservices.orderservice.response.SuccessResponse.Created;
import com.microservices.orderservice.ultils.discount.Condition;
import com.microservices.orderservice.ultils.discount.Discount;
import com.microservices.orderservice.ultils.discount.DiscountFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClientBuilder;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DiscountFactory discountFactory;
    private ObjectMapper objectMapper = new ObjectMapper();
    public ResponseEntity<?> order(OrderRequest request) throws JsonProcessingException {
        // Check user

        // Check product is Stock
        InventoryRequest data = InventoryRequest.builder()
                .products(request.getProducts())
                .build();
        InventoryResponse result = webClientBuilder.post()
                .uri("http://localhost:8082/api/v1/inventory/isStock/multiple")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(data)
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .block();
        // If sale out, then return Bad request
        if (!result.isStocks()) {
            OrderFailureResponse orderFailure = OrderFailureResponse.builder()
                    .productSaleOuts(result.getProductSaleOut())
                    .build();
            BadRequest response = new BadRequest("Product sale out!", orderFailure);
            return ResponseEntity.badRequest().body(response);
        }
        double price = request.getProducts()
                .stream()
                .map(OrderItem::getPrice)
                .mapToDouble(Float::doubleValue)
                .sum();
        // Get Price
        if (request.getDiscountCode() != null) {
            price = getPriceWithDiscount(request.getDiscountCode(), price);
        }
        // Create new Order
        List<OrderItem> orderItems = request.getProducts()
                .stream()
                .map(product -> new OrderItem(product.getProductId(), product.getName(),
                        product.getPrice(), product.getAuthor(), product.getQuantity()))
                .collect(Collectors.toList());
        Order order = Order.builder()
                .userId(Long.parseLong("1"))
                .products(orderItems)
                .price(price)
                .build();
        Order newOrder = orderRepository.save(order);
        OrderNotifyEvent orderNotifyEvent = new OrderNotifyEvent(
                newOrder.getProducts(),
                request.getUserEmail(),
                price
        );
        String valueSenderString = objectMapper.writeValueAsString(orderNotifyEvent);
        // Notification
        kafkaTemplate.send(
                "notificationOrderTopic",
                valueSenderString
        );
        // Decrement product in Inventory
        kafkaTemplate.send(
                "decrementInventories",
                valueSenderString
        );
        // Delete Product in Cart
        kafkaTemplate.send(
                "removeProductInCartTopic",
                valueSenderString
        );
        // Save Order into Database
        Created response = new Created("Created!", newOrder);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }
    public String cancelOrder(CancelOrderRequest request) {
        // Check User

        // Check Order
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), request.getUserId()).orElse(null);
        if (order == null) {
            return "Order is not Exist!";
        }
        else {
            orderRepository.deleteById(request.getOrderId());
            return "Cancel Order successfully!";
        }
    }
    public List<Order> getAllOrderByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }
    public double getPriceWithDiscount(String codeDiscount, double price) {
        // Check discount code in database
        // Get information discount
        Condition condition = new Condition("lesser", 100);
        InformationDiscount discount = new InformationDiscount(
                "percentage",
                "discountCode",
                100,
                0.5,
                "P50",
                "description",
                condition,
                LocalDateTime.now(),
                24,
                1
        );
        Discount discountCreated = discountFactory.createDiscount(
                discount.getType(),
                discount.getDiscountCode(),
                discount.getDiscount(),
                discount.getKey(),
                discount.getDescription(),
                discount.getCreatedAt(),
                discount.getEffectedFrom(),
                discount.getExpiresIn(),
                discount.getCondition()
        );
        // Save data of user used Discount (moi nguoi chi dung duoc 1 lan)
        return discountCreated.getPrice(price);
    }
}
