package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserDetailsConfig {

    @Value("${auth.admin.password-hash}")
    private String adminPasswordHash;

    @Value("${auth.user.password-hash}")
    private String userPasswordHash;

    @Bean
    UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(adminPasswordHash)
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(userPasswordHash)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}
