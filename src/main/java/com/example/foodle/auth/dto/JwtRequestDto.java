package com.example.foodle.auth.dto;

import lombok.Data;

@Data
public class JwtRequestDto {
    private String username;
    private String password;
}
