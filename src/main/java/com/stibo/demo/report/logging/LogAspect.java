package com.stibo.demo.report.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import static java.lang.System.currentTimeMillis;

@Aspect
@Component
@Conditional(LogAspect.Enabled.class)
public class LogAspect {
    public static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    static class Enabled implements Condition {
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return LOG.isDebugEnabled();
        }
    }

    @Around("@annotation(com.stibo.demo.report.logging.LogTime)")
    public Object logTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var start = currentTimeMillis();
        var result = proceedingJoinPoint.proceed();
        var method = proceedingJoinPoint.getSignature();
        LOG.debug("{}.{}: {} ms", method.getDeclaringType().getName(), method.getName(), currentTimeMillis() - start);
        return result;
    }
}
