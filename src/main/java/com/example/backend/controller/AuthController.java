package com.example.backend.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.common.ResultResponse;
import com.example.backend.security.JwtTokenProvider;
import com.example.backend.vo.LoginRequest;
import com.example.backend.vo.LoginResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResultResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        String accessToken = jwtTokenProvider.createAccessToken(userDetails.getUsername(), role);

        LoginResponse response = LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .username(userDetails.getUsername())
                .role(role)
                .build();

        return ResultResponse.ok(response);
    }

    @GetMapping("/me")
    public ResultResponse<LoginResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        LoginResponse response = LoginResponse.builder()
                .accessToken(null)
                .tokenType("Bearer")
                .username(userDetails.getUsername())
                .role(role)
                .build();

        return ResultResponse.ok(response);
    }
}
