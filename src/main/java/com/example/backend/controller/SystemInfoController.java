package com.example.backend.controller;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.time.Instant;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.SystemInfoResponse;

@RestController
@RequestMapping("/api/v1/system")
@CrossOrigin(origins = "*")
public class SystemInfoController {

    @GetMapping("/info")
    public SystemInfoResponse info() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMemoryMb = runtime.maxMemory() / (1024 * 1024);
        long uptimeSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000;

        return SystemInfoResponse.builder()
                .status("UP")
                .timestamp(Instant.now().toString())
                .nodeName(resolveNodeName())
                .javaVersion("21")
                .usedMemoryMb(usedMemoryMb)
                .maxMemoryMb(maxMemoryMb)
                .uptimeSeconds(uptimeSeconds)
                .build();
    }

    private String resolveNodeName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
