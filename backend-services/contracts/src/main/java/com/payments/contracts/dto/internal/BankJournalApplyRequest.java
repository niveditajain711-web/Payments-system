package com.payments.contracts.dto.internal;

import java.util.List;

public record BankJournalApplyRequest(
        String npciTxnId,
        String idempotencyKey,
        List<LedgerLineDto> lines
) {
    public record LedgerLineDto(
            String accountId,
            long debitPaise,
            long creditPaise
    ) {}
}
