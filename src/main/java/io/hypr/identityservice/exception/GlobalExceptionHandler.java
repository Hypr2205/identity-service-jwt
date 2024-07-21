package io.hypr.identityservice.exception;

import io.hypr.identityservice.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity
            .badRequest()
            .body(ApiResponse
                .<String>builder()
                .code(1001)
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<String>> handleUncategorizedException(Exception e) {
        return ResponseEntity
            .badRequest()
            .body(ApiResponse
                .<String>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String enumKey = Objects
            .requireNonNull(e
                .getFieldError())
            .getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
        
        return ResponseEntity
            .badRequest()
            .body(ApiResponse
                .<String>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<String>> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .badRequest()
            .body(ApiResponse
                .<String>builder()
                .code(e
                    .getErrorCode()
                    .getCode())
                .message(errorCode.getMessage())
                .build());
    }
}
