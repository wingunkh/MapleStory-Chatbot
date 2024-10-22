package com.maple.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * AOP를 이용하여 컨트롤러 메서드의 실행 시간을 로깅하는 클래스
 * - Aspect: 공통 관심사를 모듈화한 것
 * - Join Point: Aspect가 적용될 수 있는 특정 지점
 * - Advice: Aspect의 기능을 정의한 코드
 */
@Aspect
@Component
@Slf4j
public class LoggingAop {
    /**
     * 컨트롤러 패키지 내 모든 메서드의 Join Point를 정의하는 메서드
     */
    @Pointcut("execution(* com.maple.controller.*.*(..))")
    private void controllers() {}

    /**
     * 메서드의 실행을 감싸서 실행 시간을 로깅하는 메서드
     * @param joinPoint 실행 중인 Join Point 정보
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생할 수 있는 예외
     */
    @Around("controllers()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();

        log.info("{}() Execution Duration: {} ms", joinPoint.getSignature().getName(), System.currentTimeMillis() - startTime);

        return result;
    }
}
