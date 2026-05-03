package com.nadson.myfinance.infrastructure.adapter.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "revinfo_custom")
@RevisionEntity(AuditRevisionListener.class)
public class AuditRevisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "rev")
    private int rev;

    @RevisionTimestamp
    @Column(name = "revtstmp")
    private long revtstmp;

    @Column(name = "user_id")
    private String userId;

    public int getRev() { return rev; }
    public void setRev(int rev) { this.rev = rev; }

    public long getRevtstmp() { return revtstmp; }
    public void setRevtstmp(long revtstmp) { this.revtstmp = revtstmp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}