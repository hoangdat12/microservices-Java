package com.microservices.orderservice.repository;

import com.microservices.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Long userId);

    Optional<Order> findByIdAndUserId(Long id, Long userId);
}
