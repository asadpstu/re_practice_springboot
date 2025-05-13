package com.example.practice_application.service;

import com.example.practice_application.Utils.TokenValidation;
import com.example.practice_application.dto.OrderStatusDto;
import com.example.practice_application.enums.OrderStatus;
import com.example.practice_application.model.*;
import com.example.practice_application.repository.CartRepository;
import com.example.practice_application.repository.OrderItemRepository;
import com.example.practice_application.repository.OrdersRepository;
import com.example.practice_application.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
@Transactional
public class OrderService {
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TokenValidation tokenValidation;
    @Autowired
    private CartRepository cartRepository;


    public Orders createOrder(HttpServletRequest request) {
        User user = tokenValidation.extractUserFromRequest(request);
        List<Cart> carts = cartRepository.findAllByUserId(user.getId());
        Orders orderCreated = null;
        if (!carts.isEmpty()) {
            Orders newOrder = new Orders();
            newOrder.setUser(user);
            orderCreated = ordersRepository.save(newOrder);
            for (Cart cart : carts) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(cart.getProduct());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setPrice(cart.getPrice());
                orderItem.setTotalPrice(cart.getTotal_price());
                orderItem.setOrders(orderCreated);
                orderItemRepository.save(orderItem);
                cartRepository.delete(cart);
            }

            List<OrderItem> orderItems = orderItemRepository.findByOrders(orderCreated);
            orderCreated.setOrderItems(orderItems);
            ordersRepository.save(orderCreated);
        }
        return orderCreated;
    }

    public List<Orders> getMyOrder(HttpServletRequest request) {
        User user = tokenValidation.extractUserFromRequest(request);
        return ordersRepository.findAllByUser(user);
    }

    public String updateOrderStatus(HttpServletRequest request, OrderStatusDto orderStatusDto) {
        tokenValidation.extractUserFromRequest(request);
        Orders order = ordersRepository.findById(orderStatusDto.getOrderId()).orElse(null);
        if (order == null) {
            return "Order not found";
        }
        order.setStatus(orderStatusDto.getOrderStatus());
        ordersRepository.save(order);
        return "Order updated";
    }

    public List<Orders> searchOrders(String orderStatusStr, String orderBy, int page, int size) {
        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(orderStatusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + orderStatusStr);
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if ("DESC".equalsIgnoreCase(orderBy)) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, "createdAt"));
        Page<Orders> ordersPage = ordersRepository.searchOrders(orderStatus, pageable);
        return ordersPage.getContent();
    }

}
