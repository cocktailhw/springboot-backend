package com.example.backend.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

/**
 * 첨부파일 오브젝트 스토리지(S3/MinIO) 연동.
 */
public interface FileStorageService {

    StoredFile store(MultipartFile file);

    InputStream download(String storedFileName);

    long getContentLength(String storedFileName);

    void deleteQuietly(String storedFileName);

    record StoredFile(String originalFileName, String storedFileName, long fileSize) {
    }
}
