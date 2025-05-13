package com.example.practice_application.repository;

import com.example.practice_application.model.Cart;
import com.example.practice_application.model.Product;
import com.example.practice_application.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserAndProduct(User user, Product product);

    @Query("select s from Cart s where s.user.id=:userId")
    List<Cart> findAllByUserId(int userId);

    void deleteCartByUserAndProduct(User user, Product product);
}
