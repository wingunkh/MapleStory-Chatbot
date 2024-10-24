package com.maple.exception;

import com.maple.service.ExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller 클래스의 전역 예외 처리를 위한 클래스
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class RestControllerExceptionHandler {
    private final ExceptionService exceptionService;

    /**
     * 모든 예외를 처리하는 메서드
     * @return 오류 메시지
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException() {
        String errorMessage = String.join("\n",
                "알 수 없는 오류가 발생했습니다. 개발자에게 문의해 주세요!",
                "99gusrms@naver.com"
        );

        return ResponseEntity.status(HttpStatus.OK).body(exceptionService.createErrorMessage(errorMessage));
    }
}
