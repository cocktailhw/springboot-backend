package com.example.backend.common;

/**
 * 동일 요청이 짧은 간격으로 중복 접수된 경우 발생한다.
 */
public class DuplicateRequestException extends RuntimeException {

    public DuplicateRequestException(String message) {
        super(message);
    }
}
