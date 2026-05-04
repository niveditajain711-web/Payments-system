package com.payments.contracts.dto;

public record VpaRegisterResponse(
        String vpa,
        String bankCode,
        String accountId
) {}
