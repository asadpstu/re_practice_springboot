package com.example.practice_application.dto;
import com.example.practice_application.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserResponseDto {
    private int id;
    private String name;
    private String username;
    private int age;
    private UserRole role;
    private String details;
    private String email;
    private String phone;
    private AddressDto address;
    private LocalDate updatedAt;
}
