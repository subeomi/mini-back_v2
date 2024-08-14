package org.gbsb.duncool.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@Log4j2
public class ControllerReqLogging {

    @Before("execution(* org.gbsb.duncool.controller.DunCoolController.*(..))")
    public void logReqInfo(JoinPoint jp) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // ip
        String ip = request.getRemoteAddr();

        // 메소드명
        String methodName = jp.getSignature().getName();

        // 매개변수
        Object[] args = jp.getArgs();

        log.info("-======- Request -=====-");
        log.info("IP: " + ip);
        log.info("Method Name: " + methodName);
        log.info("Args: " + Arrays.toString(args));
    }
}
