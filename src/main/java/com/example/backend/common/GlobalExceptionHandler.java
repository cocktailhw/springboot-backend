package com.example.backend.common;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResultResponse<Void>> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResultResponse.fail(404, "NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResultResponse<Void>> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResultResponse.fail(404, "NOT_FOUND", "요청하신 리소스를 찾을 수 없습니다."));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResultResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResultResponse.fail(401, "BAD_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResultResponse<Void>> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResultResponse.fail(401, "UNAUTHORIZED", "인증에 실패하였습니다."));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResultResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResultResponse.fail(403, "FORBIDDEN", "접근 권한이 없습니다."));
    }

    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<ResultResponse<Void>> handleDuplicateRequest(DuplicateRequestException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResultResponse.fail(409, "DUPLICATE_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResultResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.fail(400, "INVALID_REQUEST_BODY",
                        "요청 본문을 해석할 수 없습니다. 항목 유형은 NOTICE 만 사용할 수 있습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResultResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.fail(400, "VALIDATION_ERROR",
                        message.isBlank() ? "입력값 검증에 실패하였습니다." : message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResultResponse<Void>> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.fail(400, "MISSING_PARAMETER",
                        "필수 파라미터 '" + ex.getParameterName() + "' 가 누락되었습니다."));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResultResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.fail(400, "TYPE_MISMATCH",
                        "파라미터 '" + ex.getName() + "' 값이 올바르지 않습니다."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResultResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResultResponse.fail(400, "BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse<Void>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResultResponse.fail(500, "INTERNAL_ERROR",
                        "시스템 오류가 발생하였습니다. 관리자에게 문의하세요."));
    }
}
