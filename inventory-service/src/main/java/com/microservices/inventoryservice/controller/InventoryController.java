package com.microservices.inventoryservice.controller;

import com.microservices.inventoryservice.dto.InventoryRequest;
import com.microservices.inventoryservice.dto.MultipleCheckStockRequest;
import com.microservices.inventoryservice.dto.MultipleCheckStockResponse;
import com.microservices.inventoryservice.model.Inventory;
import com.microservices.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;
    @GetMapping("/isStock/{productId}")
    public ResponseEntity<Boolean> isStock(@PathVariable String productId) {
        return new ResponseEntity<>(inventoryService.isStock(productId), HttpStatus.OK);
    }
    @PostMapping("/isStock/multiple")
    public ResponseEntity<MultipleCheckStockResponse> isStocks(@RequestBody MultipleCheckStockRequest request) {
        MultipleCheckStockResponse response = inventoryService.isStocks(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody InventoryRequest request) {
        Inventory inventory = inventoryService.createInventory(request);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }
    @PostMapping("/increment")
    public ResponseEntity<?> incrementProductInInventory(@RequestBody InventoryRequest request) {
        Inventory inventory = inventoryService.incrementProductInInventory(request);
        if (inventory == null) {
            return new ResponseEntity<>("Product not found!", HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(inventory, HttpStatus.OK);
        }
    }
}
