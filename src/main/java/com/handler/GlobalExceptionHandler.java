package com.handler;

import com.exception.DeepSeekApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeepSeekApiException.class)
    public ResponseEntity<String> handleDeepSeekApiException(DeepSeekApiException e) {
        // 记录日志，方便后续排查问题
        System.err.println("捕获到 DeepSeekApiException 异常: " + e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 可以添加其他异常处理方法，例如处理通用的 Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        System.err.println("捕获到通用异常: " + e.getMessage());
        return new ResponseEntity<>("系统发生未知错误，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}