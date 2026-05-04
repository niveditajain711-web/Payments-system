package com.payments.contracts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PayResponse(
        String npciTxnId,
        String status,
        String npciResponseCode,
        String message
) {
    public static PayResponse success(String npciTxnId) {
        return new PayResponse(npciTxnId, "SUCCESS", "00", null);
    }

    public static PayResponse failure(String code, String message) {
        return new PayResponse(null, "FAILED", code, message);
    }
}
