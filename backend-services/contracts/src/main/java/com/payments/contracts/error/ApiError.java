package com.payments.contracts.error;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,
        String message,
        String npciResponseCode
) {
    public static ApiError of(String code, String message) {
        return new ApiError(code, message, null);
    }

    public static ApiError npci(String message, String npciResponseCode) {
        return new ApiError("NPCI_ERROR", message, npciResponseCode);
    }
}
