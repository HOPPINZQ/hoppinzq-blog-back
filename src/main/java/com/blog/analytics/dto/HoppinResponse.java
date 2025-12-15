package com.blog.analytics.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 统一API响应格式
 *
 * @author hoppinzq
 * @since 2025-12-12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HoppinResponse<T> {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 成功响应（无数据）
     */
    public static <T> HoppinResponse<T> success() {
        return HoppinResponse.<T>builder()
                .success(true)
                .message("操作成功")
                .timestamp(System.currentTimeMillis())
                .code(200)
                .build();
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> HoppinResponse<T> success(String message) {
        return HoppinResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .code(200)
                .build();
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> HoppinResponse<T> success(T data) {
        return HoppinResponse.<T>builder()
                .success(true)
                .message("操作成功")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .code(200)
                .build();
    }

    /**
     * 成功响应（带消息和数据）
     */
    public static <T> HoppinResponse<T> success(String message, T data) {
        return HoppinResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .code(200)
                .build();
    }

    /**
     * 失败响应
     */
    public static <T> HoppinResponse<T> fail(String message) {
        return HoppinResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .code(500)
                .build();
    }

    /**
     * 失败响应（带错误码）
     */
    public static <T> HoppinResponse<T> fail(Integer code, String message) {
        return HoppinResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .code(code)
                .build();
    }

    /**
     * 失败响应（带数据和错误码）
     */
    public static <T> HoppinResponse<T> fail(Integer code, String message, T data) {
        return HoppinResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .code(code)
                .build();
    }
}