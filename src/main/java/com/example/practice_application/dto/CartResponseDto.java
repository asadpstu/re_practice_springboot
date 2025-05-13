package com.example.practice_application.dto;

import com.example.practice_application.model.Product;
import com.example.practice_application.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private Long id;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private LocalDateTime updatedAt;
    private Product product;

}
