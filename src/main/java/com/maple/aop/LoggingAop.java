package com.maple.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/*
    Aspect: 공통 관심사 (Cross-Cutting-Concerns)를 모듈화한 것
    Join Point: Aspect가 적용될 수 있는 특정 지점
    Advice: Aspect의 기능을 정의한 것
*/
@Aspect
@Component
@Slf4j
public class LoggingAop {
    // @Pointcut: Advice가 적용될 Join Point를 결정
    // execution: 메서드 실행 Join Point를 매칭
    // * com.maple.controller.*.*(..)) : com.maple.controller 패키지 내의 모든 클래스 내의 모든 메서드 (반환 타입과 메서드 시그니처와 관계없이)
    @Pointcut("execution(* com.maple.controller.*.*(..))")
    private void controllers() {}

    @Pointcut("execution(* com.maple.batch.*.*(..))")
    private void batches() {}

    @Pointcut("controllers() || batches()")
    private void logging() {}

    // @Around: 메서드 실행을 감싸서 제어할 수 있는 Advice 유형
    @Around("logging()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 메서드 실행
        Object proceed = joinPoint.proceed();

        // 메서드 시그니처와 실행 시간을 로깅
        log.info("{}() 메서드 실행 시간 : {} ms", joinPoint.getSignature().getName(), System.currentTimeMillis() - System.currentTimeMillis());

        // 메서드 실행 결과 반환
        return proceed;
    }
}