package com.example.practice_application.service;

import com.example.practice_application.dto.CartDto;
import com.example.practice_application.dto.CartResponseDto;
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

import java.math.BigDecimal;
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

    public CartResponseDto addCart(HttpServletRequest request,  CartDto cartPayload) {
        String token =  request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUserName(token);
        User user = userService.getUserByUserName(username);
        Optional<Product> product = productRepository.findById(cartPayload.getProduct_id());

        if (user == null  || product.isEmpty()) {
            throw new IllegalArgumentException("Invalid user or product ID");
        }

        /* Check if there is existing cart for same product and user */
        Cart existingCart = cartRepository.findByUserAndProduct(user, product);
        if (existingCart != null) {
            existingCart.setQuantity(cartPayload.getQuantity() + existingCart.getQuantity());
            existingCart.setPrice(product.get().getPrice());
            existingCart.setTotal_price(product.get().getPrice().multiply(BigDecimal.valueOf(existingCart.getQuantity())));
            var updatedCart = cartRepository.save(existingCart);
            return cartResponse(updatedCart);
        }
        else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setProduct(product.get());
            newCart.setQuantity(cartPayload.getQuantity());
            newCart.setPrice(product.get().getPrice());
            newCart.setTotal_price(product.get().getPrice().multiply(BigDecimal.valueOf(cartPayload.getQuantity())));
            var addedCart = cartRepository.save(newCart);
            return cartResponse(addedCart);
        }
    }


    private CartResponseDto cartResponse(Cart cart) {
        CartResponseDto cartResponseDto = new CartResponseDto();
        cartResponseDto.setId(cart.getId());
        cartResponseDto.setProduct(cart.getProduct());
        cartResponseDto.setUser(cart.getUser());
        cartResponseDto.setQuantity(cart.getQuantity());
        cartResponseDto.setPrice(cart.getPrice());
        cartResponseDto.setTotalPrice(cart.getTotal_price());
        cartResponseDto.setUpdatedAt(cart.getUpdatedAt());
        return cartResponseDto;
    }
}
