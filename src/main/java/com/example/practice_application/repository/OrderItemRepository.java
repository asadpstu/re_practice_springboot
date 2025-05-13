package com.example.practice_application.repository;

import com.example.practice_application.model.OrderItem;
import com.example.practice_application.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrders(Orders orderCreated);
}
