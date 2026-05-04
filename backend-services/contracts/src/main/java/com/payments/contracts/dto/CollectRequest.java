package com.payments.contracts.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CollectRequest(
        @NotBlank String payeeVpa,
        @NotBlank String payerVpa,
        @NotNull @Min(1) Long amountPaise,
        String note
) {}
