package org.example.ec2confi.controller;


import lombok.RequiredArgsConstructor;
import org.example.ec2confi.dto.*;
import org.example.ec2confi.entity.User;
import org.example.ec2confi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        userService.registerUser(request);

        return ResponseEntity.ok(
                new AuthResponse("Register success", request.getUsername())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        userService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                new AuthResponse("Login success", request.getUsername())
        );
    }

    @GetMapping("/ping")
    public String ping(){
        return "Auth API is working";
    }
}
