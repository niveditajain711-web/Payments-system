package com.payments.bank.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "idempotency_records")
public class IdempotencyEntity {

    @Id
    @Column(length = 128)
    private String idempotencyKey;

    @Column(name = "npci_txn_id", nullable = false)
    private String npciTxnId;

    @Column(name = "response_status", nullable = false)
    private String responseStatus;

    @Column(name = "response_code", length = 8)
    private String responseCode;

    @Column(name = "response_message", length = 512)
    private String responseMessage;

    protected IdempotencyEntity() {}

    public IdempotencyEntity(String idempotencyKey, String npciTxnId, String responseStatus, String responseCode, String responseMessage) {
        this.idempotencyKey = idempotencyKey;
        this.npciTxnId = npciTxnId;
        this.responseStatus = responseStatus;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getNpciTxnId() {
        return npciTxnId;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
