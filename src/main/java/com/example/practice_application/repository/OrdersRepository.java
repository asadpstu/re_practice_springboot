package com.example.practice_application.repository;

import com.example.practice_application.enums.OrderStatus;
import com.example.practice_application.model.OrderItem;
import com.example.practice_application.model.Orders;
import com.example.practice_application.model.Product;
import com.example.practice_application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Orders findByUser(User user);

    List<Orders> findAllByUser(User user);

    @Query("SELECT o FROM Orders o WHERE o.status = :order_status")
    Page<Orders> searchOrders(@Param("order_status") OrderStatus order_status, Pageable pageable);


}
