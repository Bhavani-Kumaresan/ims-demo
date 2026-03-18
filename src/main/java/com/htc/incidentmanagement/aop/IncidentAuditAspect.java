package com.htc.incidentmanagement.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IncidentAuditAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncidentAuditAspect.class);

    @Before("execution(* com.htc.incidentmanagement.service..*.*(..))")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        LOGGER.info(
                "Entering method: {}.{}()",
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    @Pointcut("execution(* com.htc.incidentmanagement.service..*(..))")
    public void incidentServiceLayer() {
        // Pointcut definition
    }

    @Pointcut("execution(* com.htc.incidentmanagement.controller..*(..))")
    public void incidentControllerLayer() {
        // Pointcut definition
    }

    @Pointcut("incidentServiceLayer() || incidentControllerLayer()")
    public void incidentApplicationFlow() {
        // Combined pointcut
    }

    @AfterThrowing(pointcut = "incidentApplicationFlow()", throwing = "exception")
    public void logIncidentException(JoinPoint joinPoint, Exception exception) {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        LOGGER.error(
                "Incident processing failure in {}.{}() | Reason: {}",
                className,
                methodName,
                exception.getMessage(),
                exception);
    }
}
