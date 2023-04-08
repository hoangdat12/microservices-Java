package com.microservices.inventoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.inventoryservice.dto.*;
import com.microservices.inventoryservice.repository.InventoryRepository;
import com.microservices.inventoryservice.model.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    public Boolean isStock(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId).orElse(null);
        return inventory != null;
    }
    public MultipleCheckStockResponse isStocks(MultipleCheckStockRequest request) {
        List<String> productIds = (List<String>) request.getProducts()
                .stream()
                .map(product -> product.getProductId())
                .collect(Collectors.toList());;
        List<Inventory> inventories = inventoryRepository.findByProductIdIn(productIds);
        List<Product> productSaleOut = new ArrayList<>();
        for (Product product : request.getProducts()) {
            int requestedQuantity = product.getQuantity();
            int availableQuantity = inventories.stream()
                    .filter(inventory -> inventory.getProductId().equals(product.getProductId()))
                    .mapToInt(Inventory::getQuantity)
                    .sum();
            if (requestedQuantity > availableQuantity) {
                productSaleOut.add(product);
            }
        }
        MultipleCheckStockResponse response = MultipleCheckStockResponse.builder()
                .isStocks(productSaleOut.isEmpty() ? true : false)
                .productSaleOut(productSaleOut.isEmpty() ? Collections.emptyList() : productSaleOut)
                .build();
        return response;
    }
    public Inventory createInventory(InventoryRequest request) {
        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();
        return inventoryRepository.save(inventory);
    }
    public Inventory incrementProductInInventory(InventoryRequest request) {
        // Check User is Admin

        // IF not Admin return ;
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId()).orElse(null);
        if (inventory == null) {
            return null;
        }
        else {
            inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
            return inventoryRepository.save(inventory);
        }
    }
    @KafkaListener(topics = "decrementInventories", groupId = "inventoryId")
    public void decrementProductInventory(String data) throws JsonProcessingException {
        ReceiveFromOrder receive = objectMapper.readValue(data, ReceiveFromOrder.class);
        inventoryRepository.decrementQuantities(receive.getProducts());
    }
}
