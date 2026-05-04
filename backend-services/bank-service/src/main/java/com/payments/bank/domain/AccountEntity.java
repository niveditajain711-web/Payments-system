package com.payments.bank.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    public enum Kind {
        USER, CLEARING
    }

    @Id
    private UUID id;

    @Column(unique = true)
    private String vpa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kind kind;

    @Column(name = "balance_paise", nullable = false)
    private long balancePaise;

    @Column(nullable = false)
    private String label;

    protected AccountEntity() {}

    public AccountEntity(UUID id, String vpa, Kind kind, long balancePaise, String label) {
        this.id = id;
        this.vpa = vpa;
        this.kind = kind;
        this.balancePaise = balancePaise;
        this.label = label;
    }

    public UUID getId() {
        return id;
    }

    public String getVpa() {
        return vpa;
    }

    public Kind getKind() {
        return kind;
    }

    public long getBalancePaise() {
        return balancePaise;
    }

    public void setBalancePaise(long balancePaise) {
        this.balancePaise = balancePaise;
    }

    public String getLabel() {
        return label;
    }
}
