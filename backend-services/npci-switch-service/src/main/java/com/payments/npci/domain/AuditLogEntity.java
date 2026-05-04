package com.payments.npci.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLogEntity {

    @Id
    private UUID id;

    @Column(name = "npci_txn_id", nullable = false)
    private String npciTxnId;

    @Column(name = "correlation_id")
    private String correlationId;

    @Column(nullable = false)
    private String operation;

    private String detail;

    @Column(name = "response_code", length = 8)
    private String responseCode;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected AuditLogEntity() {}

    public AuditLogEntity(UUID id, String npciTxnId, String correlationId, String operation, String detail, String responseCode) {
        this.id = id;
        this.npciTxnId = npciTxnId;
        this.correlationId = correlationId;
        this.operation = operation;
        this.detail = detail;
        this.responseCode = responseCode;
    }
}
