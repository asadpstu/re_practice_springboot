package com.example.practice_application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Component
public class LoginResponseDto {
    private String token;
    private String refreshToken;
    private String message;
}
