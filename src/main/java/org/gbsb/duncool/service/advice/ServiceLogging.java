package org.gbsb.duncool.service.advice;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class ServiceLogging {

    @Around("execution(* org.gbsb.duncool.service.*.*(..))")
    public Object serviceExecutionTimeInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();  // 메소드 실행 전 시간 기록

        try {
            Object result = joinPoint.proceed();  // 실제 메소드 실행
            long executionTime = System.currentTimeMillis() - start;  // 메소드 실행 후 시간 차이 계산

            log.info("{} executed in {}ms", joinPoint.getSignature(), executionTime);
            return result;
        } catch (Throwable throwable) {
            log.error("Exception in {}", joinPoint.getSignature(), throwable);
            throw throwable;  // 예외를 다시 던져서 원래 호출 스택이 유지되도록 함
        }
    }
}
