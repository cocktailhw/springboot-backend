package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    private static final String ACCESS_TOKEN_COOKIE_SCHEME = "accessTokenCookie";

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portal API")
                        .description("행복시 포털 백엔드 API. JWT는 HttpOnly 쿠키(accessToken)로 전달됩니다.")
                        .version("v1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(ACCESS_TOKEN_COOKIE_SCHEME, new SecurityScheme()
                                .name("accessToken")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("로그인 시 발급되는 HttpOnly JWT 쿠키")))
                .addSecurityItem(new SecurityRequirement().addList(ACCESS_TOKEN_COOKIE_SCHEME));
    }
}
