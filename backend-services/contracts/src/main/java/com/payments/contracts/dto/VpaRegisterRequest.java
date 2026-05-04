package com.payments.contracts.dto;

import jakarta.validation.constraints.NotBlank;

public record VpaRegisterRequest(
        @NotBlank String vpa,
        @NotBlank String displayName,
        @NotBlank String bankCode,
        /** Ledger account UUID at the target bank (demo: create account via bank ops or use seeded IDs). */
        @NotBlank String accountId
) {}
