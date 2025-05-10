package com.example.practice_application.service;

import com.example.practice_application.dto.CartDto;
import com.example.practice_application.jwt.JWTService;
import com.example.practice_application.model.Cart;
import com.example.practice_application.model.Product;
import com.example.practice_application.model.User;
import com.example.practice_application.repository.CartRepository;
import com.example.practice_application.repository.ProductRepository;
import com.example.practice_application.repository.UserRepository;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    public Cart addCart(HttpServletRequest request,  CartDto cartPayload) {
        String token =  request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUserName(token);
        User user = userService.getUserByUserName(username);
        Optional<Product> product = productRepository.findById(cartPayload.getProduct_id());

        if (user == null  || product.isEmpty()) {
            throw new IllegalArgumentException("Invalid user or product ID");
        }

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setProduct(product.get());
        newCart.setPrice(product.get().getPrice());
        newCart.setQuantity(cartPayload.getQuantity());
        return cartRepository.save(newCart);
    }
}
