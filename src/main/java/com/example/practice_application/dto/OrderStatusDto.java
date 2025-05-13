package com.example.practice_application.dto;

import com.example.practice_application.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderStatusDto {
    private Long orderId;
    private OrderStatus orderStatus;
}
