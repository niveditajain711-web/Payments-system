package com.payments.contracts.dto.internal;

public record VpaResolveResponse(
        String vpa,
        String bankCode,
        String accountId
) {}
