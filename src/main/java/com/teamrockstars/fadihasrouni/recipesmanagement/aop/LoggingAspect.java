package com.teamrockstars.fadihasrouni.recipesmanagement.aop;

import com.teamrockstars.fadihasrouni.recipesmanagement.service.RecipeService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logging aspect: this mainly fires on each method enter and return for service packages
 *
 */
@Aspect
@Component
public class LoggingAspect {

    Logger log = LoggerFactory.getLogger(RecipeService.class);

    /**
     * Setup @Pointcut declaration for Service package
     */
    @Pointcut("execution(* com.teamrockstars.fadihasrouni.recipesmanagement.service.*.*(..))")
    private void forServicePackage() {

    }

    @Pointcut("forServicePackage()")
    private void forAppFlow() {

    }

    /**
     * This method fires before each point cut and applies logging logic
     *
     * @param joinPoint
     */
    @Before("forAppFlow()")
    public void before(JoinPoint joinPoint) {
        String theMethod = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("Enter method {}, Params:{}", theMethod, new Object[] { args });
    }

    /**
     * This method fires after each point cut and applies function return logging
     * logic
     *
     * @param joinPoint
     * @param returnValue
     */
    @AfterReturning(pointcut = "forAppFlow()", returning = "returnValue")
    public void after(JoinPoint joinPoint, Object returnValue) {
        String theMethod = joinPoint.getSignature().toShortString();
        if (returnValue == null) {
            returnValue = "";
        }
        log.info("Return method {}, Result: {}", theMethod, returnValue);
    }
}

