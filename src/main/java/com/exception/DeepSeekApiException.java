package com.exception;

// 自定义与 DeepSeek API 通信相关的异常类
public class DeepSeekApiException extends RuntimeException {
    public DeepSeekApiException(String message) {
        super(message);
    }
}