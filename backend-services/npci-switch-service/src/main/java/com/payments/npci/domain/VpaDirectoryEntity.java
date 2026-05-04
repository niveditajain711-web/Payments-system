package com.payments.npci.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "vpa_directory")
public class VpaDirectoryEntity {

    @Id
    @Column(length = 128)
    private String vpa;

    @Column(name = "bank_code", nullable = false, length = 8)
    private String bankCode;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    protected VpaDirectoryEntity() {}

    public VpaDirectoryEntity(String vpa, String bankCode, UUID accountId) {
        this.vpa = vpa;
        this.bankCode = bankCode;
        this.accountId = accountId;
    }

    public String getVpa() {
        return vpa;
    }

    public String getBankCode() {
        return bankCode;
    }

    public UUID getAccountId() {
        return accountId;
    }
}
