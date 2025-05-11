package com.example.practice_application.repository;

import com.example.practice_application.model.Cart;
import com.example.practice_application.model.Product;
import com.example.practice_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserAndProduct(User user, Optional<Product> attr0);
}
