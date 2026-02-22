package org.example.ec2confi.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
