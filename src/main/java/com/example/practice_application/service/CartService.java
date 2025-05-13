package com.example.practice_application.service;

import com.example.practice_application.Utils.TokenValidation;
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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TokenValidation tokenValidation;



    public List<CartResponseDto> addCart(HttpServletRequest request, CartDto cartPayload) {
        User user = tokenValidation.extractUserFromRequest(request);
        Product product = getProductById(cartPayload.getProduct_id());
        if(product == null){
            throw new RuntimeException("Product not found");
        }

        Cart existingCart = cartRepository.findByUserAndProduct(user, product);
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + cartPayload.getQuantity());
            existingCart.setPrice(product.getPrice());
            existingCart.setTotal_price(product.getPrice().multiply(BigDecimal.valueOf(existingCart.getQuantity())));
            cartRepository.save(existingCart);
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setProduct(product);
            newCart.setQuantity(cartPayload.getQuantity());
            newCart.setPrice(product.getPrice());
            newCart.setTotal_price(product.getPrice().multiply(BigDecimal.valueOf(cartPayload.getQuantity())));
            cartRepository.save(newCart);
        }

        List<Cart> carts = cartRepository.findAllByUserId(user.getId());
        return carts.stream().map(this::mapToCartResponseDto).collect(Collectors.toList());
    }

    public List<CartResponseDto> changeCartQuantity(HttpServletRequest request, CartDto cartPayload) {
        User user = tokenValidation.extractUserFromRequest(request);
        Product product = getProductById(cartPayload.getProduct_id());

        if (cartPayload.getQuantity() == 0) {
            cartRepository.deleteCartByUserAndProduct(user, product);
        } else {
            Cart existingCart = cartRepository.findByUserAndProduct(user, product);
            if (existingCart != null) {
                existingCart.setQuantity(cartPayload.getQuantity());
                existingCart.setPrice(product.getPrice());
                existingCart.setTotal_price(product.getPrice().multiply(BigDecimal.valueOf(cartPayload.getQuantity())));
                cartRepository.save(existingCart);
            }
        }

        List<Cart> carts = cartRepository.findAllByUserId(user.getId());
        return carts.stream().map(this::mapToCartResponseDto).collect(Collectors.toList());
    }



    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
    }

    private CartResponseDto mapToCartResponseDto(Cart cart) {
        CartResponseDto dto = new CartResponseDto();
        dto.setId(cart.getId());
        dto.setProduct(cart.getProduct());
        dto.setQuantity(cart.getQuantity());
        dto.setPrice(cart.getPrice());
        dto.setTotalPrice(cart.getTotal_price());
        dto.setUpdatedAt(cart.getUpdatedAt());
        return dto;
    }

    public List<CartResponseDto> myCartList(HttpServletRequest request) {
        User user = tokenValidation.extractUserFromRequest(request);
        List<Cart> carts = cartRepository.findAllByUserId(user.getId());
        return carts.stream().map(this::mapToCartResponseDto).collect(Collectors.toList());
    }
}
