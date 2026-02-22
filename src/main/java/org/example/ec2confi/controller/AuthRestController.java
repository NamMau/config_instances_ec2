package org.example.ec2confi.controller;

import lombok.RequiredArgsConstructor;
import org.example.ec2confi.dto.*;
import org.example.ec2confi.security.JwtService;
import org.example.ec2confi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // ✅ REGISTER
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterRequest request) {

        userService.registerUser(request);

        String token = jwtService.generateToken(request.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(
                        "Register success",
                        request.getUsername(),
                        token
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        // ⭐ bước xác thực chuẩn Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // ⭐ generate JWT
        String token = jwtService.generateToken(request.getUsername());

        return ResponseEntity.ok(
                new AuthResponse(
                        "Login success",
                        request.getUsername(),
                        token
                )
        );
    }

    @GetMapping("/ping")
    public String ping() {
        return "Auth API is working";
    }
}