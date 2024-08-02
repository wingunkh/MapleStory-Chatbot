package com.maple.exception;

import com.maple.service.ExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class RestControllerExceptionHandler {
    private final ExceptionService exceptionService;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(exceptionService.createErrorMessage("알 수 없는 에러가 발생했습니다. 개발자에게 문의해 주세요!!!"));
    }
}
