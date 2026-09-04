package com.example.backend.service;

import com.example.backend.vo.LoginRequest;
import com.example.backend.vo.LoginResponse;
import com.example.backend.vo.MemberResponse;
import com.example.backend.vo.SignupRequest;

public interface AuthService {

    MemberResponse signup(SignupRequest request);

    LoginResult login(LoginRequest request);

    record LoginResult(String accessToken, LoginResponse response) {
    }
}
