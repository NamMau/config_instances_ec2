package org.example.ec2confi.controller;

import lombok.RequiredArgsConstructor;
import org.example.ec2confi.service.UserService;
import org.example.ec2confi.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute User user){
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/home")
    @ResponseBody
    public String home(){
        return "Login success!";
    }
}
