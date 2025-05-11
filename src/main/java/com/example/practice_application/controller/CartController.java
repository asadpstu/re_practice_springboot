package com.example.practice_application.controller;

import com.example.practice_application.dto.CartDto;
import com.example.practice_application.dto.CartResponseDto;
import com.example.practice_application.jwt.JWTService;
import com.example.practice_application.model.Cart;
import com.example.practice_application.model.User;
import com.example.practice_application.service.CartService;
import com.example.practice_application.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "Cart", description = "Cart's related endpoint.")
public class CartController {
    @Autowired
    private CartService cartService;


    @PostMapping("/cart/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CartResponseDto> addCart(HttpServletRequest request, @RequestBody CartDto cartPayload) {
        try {
            CartResponseDto cart =  cartService.addCart(request, cartPayload);
            return new ResponseEntity<>(cart, HttpStatus.CREATED);
        }
        catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }
}
