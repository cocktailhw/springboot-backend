package com.example.backend.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultResponse<T> {

    private final int status;
    private final String code;
    private final String message;
    private final T data;

    public static <T> ResultResponse<T> ok(T data) {
        return ResultResponse.<T>builder()
                .status(200)
                .code("SUCCESS")
                .message("정상적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    public static <T> ResultResponse<T> ok(String message, T data) {
        return ResultResponse.<T>builder()
                .status(200)
                .code("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResultResponse<T> fail(String code, String message) {
        return ResultResponse.<T>builder()
                .status(400)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> ResultResponse<T> fail(int status, String code, String message) {
        return ResultResponse.<T>builder()
                .status(status)
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}
