package com.example.backend.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.service.FileStorageService;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostConstruct
    void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            log.info("[MinIO] 버킷 '{}' 을 생성했습니다.", bucketName);
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                log.info("[MinIO] 버킷 '{}' 을 생성했습니다.", bucketName);
                return;
            }
            throw new IllegalStateException("MinIO 버킷 확인에 실패했습니다: " + bucketName, e);
        }
    }

    @Override
    public StoredFile store(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename()
        );
        if (originalFileName.contains("..")) {
            throw new IllegalArgumentException("파일명에 상위 경로 문자(..)를 포함할 수 없습니다.");
        }

        String storedFileName = UUID.randomUUID() + resolveExtension(originalFileName);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(storedFileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException | S3Exception e) {
            throw new IllegalStateException("파일 업로드에 실패했습니다.", e);
        }

        return new StoredFile(originalFileName, storedFileName, file.getSize());
    }

    @Override
    public InputStream download(String storedFileName) {
        validateStoredFileName(storedFileName);

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(storedFileName)
                .build();

        try {
            return s3Client.getObject(getRequest, ResponseTransformer.toInputStream());
        } catch (NoSuchKeyException e) {
            throw new EntityNotFoundException("파일을 찾을 수 없습니다.");
        } catch (S3Exception e) {
            throw new IllegalStateException("파일 조회에 실패했습니다.", e);
        }
    }

    @Override
    public long getContentLength(String storedFileName) {
        validateStoredFileName(storedFileName);

        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(storedFileName)
                .build();

        try {
            return s3Client.headObject(headRequest).contentLength();
        } catch (NoSuchKeyException e) {
            throw new EntityNotFoundException("파일을 찾을 수 없습니다.");
        } catch (S3Exception e) {
            throw new IllegalStateException("파일 정보 조회에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteQuietly(String storedFileName) {
        if (!StringUtils.hasText(storedFileName)) {
            return;
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storedFileName)
                    .build());
        } catch (S3Exception e) {
            log.warn("[MinIO] 파일 삭제 실패 storedFileName={}", storedFileName, e);
        }
    }

    private String resolveExtension(String originalFileName) {
        int dotIndex = originalFileName.lastIndexOf('.');
        return dotIndex >= 0 ? originalFileName.substring(dotIndex) : "";
    }

    private void validateStoredFileName(String storedFileName) {
        if (!StringUtils.hasText(storedFileName) || storedFileName.contains("..") || storedFileName.contains("/")
                || storedFileName.contains("\\")) {
            throw new IllegalArgumentException("저장된 파일명이 올바르지 않습니다.");
        }
    }
}
