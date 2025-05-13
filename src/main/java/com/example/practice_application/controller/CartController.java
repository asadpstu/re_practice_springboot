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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "Cart", description = "Cart's related endpoint.")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("/cart/add")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> addCart(HttpServletRequest request, @RequestBody CartDto cartPayload) {
        try {
            List<CartResponseDto> cart = cartService.addCart(request, cartPayload);
            return new ResponseEntity<>(cart, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/cart/change/quantity")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<CartResponseDto>> changeCartQuantity(HttpServletRequest request, @RequestBody CartDto cartPayload) {
        List<CartResponseDto> cart =  cartService.changeCartQuantity(request, cartPayload);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @GetMapping("/cart/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<CartResponseDto>> myCartList(HttpServletRequest request) {
        List<CartResponseDto> cart =  cartService.myCartList(request);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }
}
