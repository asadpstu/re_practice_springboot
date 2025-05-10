package com.example.practice_application.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserRequestDto {
    private String name;
    private String username;
    private String password;
    private int age;
    private String details;
    private String email;
    private String phone;
    private AddressDto address;
}
