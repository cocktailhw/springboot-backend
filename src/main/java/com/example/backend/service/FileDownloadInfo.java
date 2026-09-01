package com.example.backend.service;

import org.springframework.core.io.Resource;

public record FileDownloadInfo(Resource resource, String originalFileName, long contentLength) {
}
