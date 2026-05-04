package com.payments.contracts.dto;

import jakarta.validation.constraints.NotBlank;

/** Simulated UPI PIN — educational only; never use in production. */
public record CollectApproveRequest(
        @NotBlank String upiPinEncrypted
) {}
