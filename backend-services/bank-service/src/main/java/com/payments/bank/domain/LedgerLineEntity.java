package com.payments.bank.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "ledger_lines")
public class LedgerLineEntity {

    @Id
    private UUID id;

    @Column(name = "npci_txn_id", nullable = false)
    private String npciTxnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(name = "debit_paise", nullable = false)
    private long debitPaise;

    @Column(name = "credit_paise", nullable = false)
    private long creditPaise;

    protected LedgerLineEntity() {}

    public LedgerLineEntity(UUID id, String npciTxnId, AccountEntity account, long debitPaise, long creditPaise) {
        this.id = id;
        this.npciTxnId = npciTxnId;
        this.account = account;
        this.debitPaise = debitPaise;
        this.creditPaise = creditPaise;
    }
}
