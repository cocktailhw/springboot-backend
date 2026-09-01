package com.example.backend.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
@RequiredArgsConstructor
public class AuthController {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<ResultResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
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
                .username(userDetails.getUsername())
                .role(role)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie(accessToken, jwtExpirationMs).toString())
                .body(ResultResponse.ok(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResultResponse<Void>> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie("", 0).toString())
                .body(ResultResponse.ok("정상적으로 로그아웃되었습니다.", null));
    }

    @GetMapping("/me")
    public ResultResponse<LoginResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            throw new AuthenticationCredentialsNotFoundException("인증 정보가 없습니다.");
        }

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        LoginResponse response = LoginResponse.builder()
                .username(userDetails.getUsername())
                .role(role)
                .build();

        return ResultResponse.ok(response);
    }

    private ResponseCookie buildAccessTokenCookie(String token, long maxAgeMs) {
        return ResponseCookie.from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofMillis(maxAgeMs))
                .build();
    }
}
