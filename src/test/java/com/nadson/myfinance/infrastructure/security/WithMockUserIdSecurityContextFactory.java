package com.nadson.myfinance.infrastructure.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import java.util.Collections;

public class WithMockUserIdSecurityContextFactory implements WithSecurityContextFactory<WithMockUserId> {
    @Override
    public SecurityContext createSecurityContext(WithMockUserId annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        var auth = new UsernamePasswordAuthenticationToken(annotation.value(), null, Collections.emptyList());
        context.setAuthentication(auth);
        return context;
    }
}