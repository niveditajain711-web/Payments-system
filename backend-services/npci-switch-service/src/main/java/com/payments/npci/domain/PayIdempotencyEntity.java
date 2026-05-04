package com.payments.npci.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "pay_idempotency")
public class PayIdempotencyEntity {

    @Id
    @Column(length = 128)
    private String idempotencyKey;

    @Column(name = "npci_txn_id", nullable = false)
    private String npciTxnId;

    @Lob
    @Column(name = "response_json", nullable = false)
    private String responseJson;

    protected PayIdempotencyEntity() {}

    public PayIdempotencyEntity(String idempotencyKey, String npciTxnId, String responseJson) {
        this.idempotencyKey = idempotencyKey;
        this.npciTxnId = npciTxnId;
        this.responseJson = responseJson;
    }

    public String getResponseJson() {
        return responseJson;
    }
}
