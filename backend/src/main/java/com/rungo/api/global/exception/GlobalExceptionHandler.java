package com.rungo.api.global.exception;

import com.rungo.api.global.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 예외 처리 (CustomException)
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        log.warn("Business Exception: {}", e.getErrorCode().getMessage());
        ErrorCode ec = e.getErrorCode();

        return ResponseEntity.status(ec.getStatus())
                             .body(ApiResponse.error(ec.getStatus(), ec.name(), ec.getMessage()));
    }

    // 2. 입력값 검증 실패 처리 (@Valid RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation Exception 발생");
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            // 중복 필드 에러 덮어쓰기 방지
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        // ErrorCode 재사용
        ErrorCode ec = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.status(ec.getStatus())
                             .body(ApiResponse.error(ec.getStatus(), ec.name(), ec.getMessage(), errors));
    }

    // 3. 파라미터 검증 실패 처리 (@Validated RequestParam, PathVariable)
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("ConstraintViolationException 발생");
        Map<String, String> errors = new HashMap<>();

        e.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            // 중복 방지
            errors.putIfAbsent(fieldName, violation.getMessage());
        });

        // ErrorCode 재사용
        ErrorCode ec = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity.status(ec.getStatus())
                             .body(ApiResponse.error(ec.getStatus(), ec.name(), ec.getMessage(), errors));
    }

    // 4. 시스템 에러 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Internal Server Error: ", e);

        ErrorCode ec = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(ec.getStatus())
                             .body(ApiResponse.error(ec.getStatus(), ec.name(), ec.getMessage()));
    }
}