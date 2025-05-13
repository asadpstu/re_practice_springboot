package com.example.practice_application.Utils;

import com.example.practice_application.jwt.JWTService;
import com.example.practice_application.model.User;
import com.example.practice_application.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenValidation {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    public User extractUserFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUserName(token);
        User user = userService.getUserByUserName(username);
        if (user == null) {
            throw new IllegalArgumentException("Invalid user");
        }
        return user;
    }
}
