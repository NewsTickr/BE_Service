//package com.newstickr.newstickr.common.aop;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//
//@Slf4j
//@Aspect
//@Component
//public class LoggingAop {
//    @Pointcut("execution(* com.newstickr.newstickr..*.*(..))")
//    private void pointcut(){}
//    @Before("pointcut()")
//    public void beforeParameterLog(JoinPoint joinPoint) {
//        // 메서드 정보 받아오기
//        Method method = getMethod(joinPoint);
//        log.info("======= method name = {} =======", method.getName());
//
//        // 파라미터 받아오기
//        Object[] args = joinPoint.getArgs();
//        if (args.length <= 0) log.info("no parameter");
//        for (Object arg : args) {
//            log.info("parameter type = {}", arg.getClass().getSimpleName());
//            log.info("parameter value = {}", arg);
//        }
//    }
//
//    // Poincut에 의해 필터링된 경로로 들어오는 경우 메서드 리턴 후에 적용
//    @AfterReturning(value = "pointcut()", returning = "returnObj")
//    public void afterReturnLog(JoinPoint joinPoint, Object returnObj) {
//        // 메서드 정보 받아오기
//        Method method = getMethod(joinPoint);
//        log.info("======= method name = {} =======", method.getName());
//        if (returnObj == null) {
//            log.info("return obj is null");
//            return;
//        }
//        log.info("return type = {}", returnObj.getClass().getSimpleName());
//        log.info("return value = {}", returnObj);
//    }
//
//    // JoinPoint로 메서드 정보 가져오기
//    private Method getMethod(JoinPoint joinPoint) {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        return signature.getMethod();
//    }
//
//}
