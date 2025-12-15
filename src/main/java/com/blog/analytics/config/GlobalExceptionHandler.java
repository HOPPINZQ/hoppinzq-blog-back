package com.blog.analytics.config;

import com.blog.analytics.dto.HoppinResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HoppinResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        log.warn("参数验证失败: {}", errorMessage);

        return ResponseEntity
                .badRequest()
                .body(HoppinResponse.fail(400, "参数验证失败: " + errorMessage));
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<HoppinResponse<Object>> handleBindException(BindException ex) {
        List<String> errors = ex.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        log.warn("参数绑定失败: {}", errorMessage);

        return ResponseEntity
                .badRequest()
                .body(HoppinResponse.fail(400, "参数绑定失败: " + errorMessage));
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HoppinResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        List<String> errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        String errorMessage = String.join(", ", errors);
        log.warn("约束验证失败: {}", errorMessage);

        return ResponseEntity
                .badRequest()
                .body(HoppinResponse.fail(400, "约束验证失败: " + errorMessage));
    }

    /**
     * 处理IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HoppinResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("参数异常: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(HoppinResponse.fail(400, ex.getMessage()));
    }

    /**
     * 处理RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<HoppinResponse<Object>> handleRuntimeException(RuntimeException ex) {
        log.error("运行时异常", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HoppinResponse.fail(500, "服务内部错误"));
    }

    /**
     * 处理通用Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HoppinResponse<Object>> handleGenericException(Exception ex) {
        log.error("未知异常", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(HoppinResponse.fail(500, "服务器内部错误"));
    }
}