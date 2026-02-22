package org.example.ec2confi.controller;


import lombok.RequiredArgsConstructor;
import org.example.ec2confi.dto.*;
import org.example.ec2confi.entity.User;
import org.example.ec2confi.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final UserService userService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request){
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());

        userService.registerUser(user);
        return new AuthResponse(
                "Register success",
                request.getUsername()
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request){
        boolean authenticated = userService.authenticate(
                        request.getUsername(),
                        request.getPassword()
        );

        if(!authenticated){
            throw new RuntimeException("Incorrect username or password");
        }

        return new AuthResponse(
                "Login success",
                request.getUsername()
        );
    }

    @GetMapping("/ping")
    public String ping(){
        return "Auth API is working";
    }
}
