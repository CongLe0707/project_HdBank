package com.example.Hdbank_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    public LoginResponse(String token) {
        this.token = token;
    }

    // getter setter
    public String getToken() {
        return token;
    }
}
