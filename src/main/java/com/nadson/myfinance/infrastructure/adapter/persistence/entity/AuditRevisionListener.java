package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity audit = (AuditRevisionEntity) revisionEntity;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth!= null && auth.getPrincipal() instanceof String) {
            audit.setUserId((String) auth.getPrincipal());
        } else {
            audit.setUserId("SYSTEM");
        }
    }
}