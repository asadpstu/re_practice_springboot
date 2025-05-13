package com.example.practice_application.controller;
import com.example.practice_application.Utils.TokenValidation;
import com.example.practice_application.dto.UserRequestDto;
import com.example.practice_application.dto.UserResponseDto;
import com.example.practice_application.model.User;
import com.example.practice_application.jwt.JWTService;
import com.example.practice_application.service.UserService;
import com.example.practice_application.dto.LoginPaylaodDto;
import com.example.practice_application.dto.LoginResponseDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin
@Tag(name = "User", description = "User's related endpoint.")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private TokenValidation tokenValidation;

    @GetMapping("")
    public ResponseEntity<String> empty(HttpServletRequest request) {
        return new ResponseEntity<>("Your session is : "+ request.getSession().getId() , HttpStatus.OK);
    }

    @PostMapping("/user/register")
    public ResponseEntity<User> createUser(@RequestBody UserRequestDto user) {
        String username = user.getUsername();
        User data= userService.getUserByUserName(username);
        if(data == null) {
            User newUser = userService.createUser(user);
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(data, HttpStatus.CONFLICT);

    }

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody LoginPaylaodDto userPayload) {
        String username = userPayload.getUsername();
        String password = userPayload.getPassword();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            String accessToken = jwtService.generateToken(username);
            String refreshToken = jwtService.generateRefreshToken(username);

            return new ResponseEntity<>(new LoginResponseDto(accessToken, refreshToken, "Login Success"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new LoginResponseDto(null, null, "Invalid Credentials"), HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/user/refresh-token")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<LoginResponseDto> refreshAccessToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>(new LoginResponseDto(null, null, "Missing or malformed Authorization header"), HttpStatus.BAD_REQUEST);
        }

        String refreshToken = authHeader.substring(7);

        if (jwtService.validateToken(refreshToken)) {
            String username = jwtService.extractUserName(refreshToken);
            String newAccessToken = jwtService.generateToken(username);
            return ResponseEntity.ok(new LoginResponseDto(newAccessToken, refreshToken, "Access token refreshed"));
        }

        return new ResponseEntity<>(new LoginResponseDto(null, null, "Invalid refresh token"), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/user/me")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        User user = tokenValidation.extractUserFromRequest(request);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @GetMapping("/users")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Optional<UserResponseDto>> getUserById(@PathVariable int id) {
        Optional<UserResponseDto> data =  userService.getUser(id);
        if(data.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


    @DeleteMapping("/users/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Optional<UserResponseDto>> removeStudent(@PathVariable int id) {
        var user = userService.getUser(id);
        if(user.isPresent()) {
            userService.deleteUser(id);
            return new ResponseEntity<>(null, HttpStatus.MOVED_PERMANENTLY);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

    }

    @GetMapping("/users/search")
    @SecurityRequirement(name = "bearerAuth")
    public List<UserResponseDto> searchUsers(@RequestParam String search) {
        return userService.searchUsers(search);
    }


}
