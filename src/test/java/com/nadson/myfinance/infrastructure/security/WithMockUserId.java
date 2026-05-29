package com.nadson.myfinance.infrastructure.security;

import org.springframework.security.test.context.support.WithSecurityContext;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockUserIdSecurityContextFactory.class)
public @interface WithMockUserId {
    String value() default "d6e3e5b0-1234-4321-8765-abcdef123456";
}