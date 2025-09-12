package com.example.todo.auth;

import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static record RegisterRequest(@NotBlank String username, @NotBlank String password) {}
    public static record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public static record JwtResponse(String token) {}
}
