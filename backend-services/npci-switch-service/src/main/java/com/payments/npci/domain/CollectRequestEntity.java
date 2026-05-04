package com.payments.npci.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "collect_requests")
public class CollectRequestEntity {

    public enum Status {
        PENDING, COMPLETED, FAILED
    }

    @Id
    private UUID collectId;

    @Column(name = "payer_vpa", nullable = false)
    private String payerVpa;

    @Column(name = "payee_vpa", nullable = false)
    private String payeeVpa;

    @Column(name = "amount_paise", nullable = false)
    private long amountPaise;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "npci_txn_id")
    private String npciTxnId;

    private String note;

    protected CollectRequestEntity() {}

    public CollectRequestEntity(UUID collectId, String payerVpa, String payeeVpa, long amountPaise, Status status, String npciTxnId, String note) {
        this.collectId = collectId;
        this.payerVpa = payerVpa;
        this.payeeVpa = payeeVpa;
        this.amountPaise = amountPaise;
        this.status = status;
        this.npciTxnId = npciTxnId;
        this.note = note;
    }

    public UUID getCollectId() {
        return collectId;
    }

    public String getPayerVpa() {
        return payerVpa;
    }

    public String getPayeeVpa() {
        return payeeVpa;
    }

    public long getAmountPaise() {
        return amountPaise;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNpciTxnId() {
        return npciTxnId;
    }

    public void setNpciTxnId(String npciTxnId) {
        this.npciTxnId = npciTxnId;
    }
}
