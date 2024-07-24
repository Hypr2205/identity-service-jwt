package io.hypr.identityservice.exception;

import io.hypr.identityservice.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min";

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
            .requireNonNull(e.getFieldError())
            .getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);

        var constraintViolation = e
            .getBindingResult()
            .getAllErrors()
            .getFirst()
            .unwrap(ConstraintViolation.class);

        var attributes = constraintViolation
            .getConstraintDescriptor()
            .getAttributes();
        log.info(attributes.toString());

        return ResponseEntity
            .badRequest()
            .body(ApiResponse
                      .<String>builder()
                      .code(errorCode.getCode())
                      .message(mapAttribute(errorCode.getMessage(), attributes))
                      .build());
    }

    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse<String>> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getStatusCode())
            .body(ApiResponse
                      .<String>builder()
                      .code(e
                                .getErrorCode()
                                .getCode())
                      .message(errorCode.getMessage())
                      .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiResponse<String>> handleAccessDeniedException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity
            .status(errorCode.getStatusCode())
            .body(ApiResponse
                      .<String>builder()
                      .code(errorCode.getCode())
                      .message(errorCode.getMessage())
                      .build());
    }

    private String mapAttribute(String message, Map<String, Object> attribute) {
        String minValue = String.valueOf(attribute.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
