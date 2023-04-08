package com.microservices.inventoryservice.repository;

import com.microservices.inventoryservice.dto.OrderItemDto;
import com.microservices.inventoryservice.dto.Product;
import com.microservices.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(String productId);
    List<Inventory> findByProductIdIn(List<String> productIds);
    @Modifying
    @Query("UPDATE Inventory SET quantity = quantity - :quantity WHERE productId = :productId")
    void decrementQuantityByProductId(
            @Param("productId") String productId,
            @Param("quantity") int quantity
    );

    @Transactional
    default void decrementQuantities(List<OrderItemDto> products) {
        for (OrderItemDto product : products) {
            decrementQuantityByProductId(product.getProductId(), product.getQuantity());
        }
    }
}
