package com.base.web.controller;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import com.base.web.core.authorize.AopAuthorizeValidator;
import com.base.web.core.exception.AuthorizeForbiddenException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class AopAuthorizeValidatorAutoConfiguration {

    @Bean
    public ControllerAuthorizeValidator controllerAuthorizeValidator() {
        return new ControllerAuthorizeValidator();
    }

    @Aspect
    @Order(1)
    static class ControllerAuthorizeValidator extends AopAuthorizeValidator {
        @Around(value = "execution(* com.base.web..controller..*Controller..*(..))||@annotation(com.base.web.core.authorize.annotation.Authorize)")
        public Object around(ProceedingJoinPoint pjp) throws Throwable {
            boolean access = super.validate(pjp);
            if (!access) throw new AuthorizeForbiddenException("无权限", 403);
            return pjp.proceed();
        }
    }
}
