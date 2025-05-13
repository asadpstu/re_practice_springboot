package com.example.practice_application.controller;

import com.example.practice_application.dto.OrderStatusDto;
import com.example.practice_application.dto.ProductResponseDto;
import com.example.practice_application.model.Orders;
import com.example.practice_application.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "Product", description = "Product's related endpoint.")
public class OrdersController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/order/create")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Orders> createOrder(HttpServletRequest request) {
        Orders orders = orderService.createOrder(request);
        if (orders == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(orders);
    }

    @GetMapping("/order/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Orders>> getMyOrder(HttpServletRequest request) {
        List<Orders> orders = orderService.getMyOrder(request);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /* For admin portal and User */
    @PutMapping("/order/status")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> updateOrderStatus(
            HttpServletRequest request,
            @RequestBody OrderStatusDto orderStatusDto
    ) {
        String staus = orderService.updateOrderStatus(request, orderStatusDto);
        return new ResponseEntity<>(staus, HttpStatus.OK);
    }

    @GetMapping("/orders")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<Orders>> getOrders(
            @RequestParam(value = "order_status", defaultValue = "PENDING") String orderStatus,
            @RequestParam(value = "order_by", defaultValue = "ASC") String orderBy,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            List<Orders> ordersList = orderService.searchOrders(orderStatus.toUpperCase(), orderBy.toUpperCase(), page, size);
            return ResponseEntity.ok(ordersList);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
