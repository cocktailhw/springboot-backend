package com.example.backend.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private final String username;
    private final String role;
}
