package com.example.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
                "status", "SUCCESS",
                "message", "Backend API Active"
        );
    }
}
