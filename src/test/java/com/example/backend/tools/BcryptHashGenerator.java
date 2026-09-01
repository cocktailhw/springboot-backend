package com.example.backend.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("AUTH_ADMIN_PASSWORD_HASH=" + encoder.encode("admin1234!"));
        System.out.println("AUTH_USER_PASSWORD_HASH=" + encoder.encode("user1234!"));
    }
}
