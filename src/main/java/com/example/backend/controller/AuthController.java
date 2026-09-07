package com.example.backend.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
import com.example.backend.service.AuthService;
import com.example.backend.vo.LoginRequest;
import com.example.backend.vo.LoginResponse;
import com.example.backend.vo.MemberResponse;
import com.example.backend.vo.SignupRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "회원가입 · 로그인 · 로그아웃 · 내 정보")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";

    private final AuthService authService;

    @Value("${jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    @Operation(summary = "회원가입", description = "신규 회원을 등록합니다. 기본 권한은 ROLE_USER 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = ResultResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 아이디")
    })
    @PostMapping("/signup")
    public ResultResponse<MemberResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResultResponse.ok("회원가입이 완료되었습니다.", authService.signup(request));
    }

    @Operation(summary = "로그인",
            description = "인증 성공 시 JWT를 HttpOnly 쿠키(accessToken)로 발급합니다. 응답 Body에는 토큰이 포함되지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 (Set-Cookie: accessToken)",
                    content = @Content(schema = @Schema(implementation = ResultResponse.class))),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<ResultResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResult loginResult = authService.login(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,
                        buildAccessTokenCookie(loginResult.accessToken(), jwtExpirationMs).toString())
                .body(ResultResponse.ok(loginResult.response()));
    }

    @Operation(summary = "로그아웃", description = "accessToken 쿠키를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(schema = @Schema(implementation = ResultResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<ResultResponse<Void>> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie("", 0).toString())
                .body(ResultResponse.ok("정상적으로 로그아웃되었습니다.", null));
    }

    @Operation(summary = "내 정보 조회", description = "쿠키 JWT로 인증된 현재 사용자 정보를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ResultResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
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
