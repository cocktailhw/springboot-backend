package com.example.backend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SystemInfoResponse {

    private final String status;
    private final String timestamp;
    private final String nodeName;
    private final String javaVersion;
    private final long usedMemoryMb;
    private final long maxMemoryMb;
    private final long uptimeSeconds;
}
