package com.payments.contracts.dto.internal;

public record BankJournalApplyResponse(
        String status,
        String npciResponseCode,
        String message
) {
    public static BankJournalApplyResponse ok() {
        return new BankJournalApplyResponse("SUCCESS", "00", null);
    }

    public static BankJournalApplyResponse fail(String code, String message) {
        return new BankJournalApplyResponse("FAILED", code, message);
    }
}
