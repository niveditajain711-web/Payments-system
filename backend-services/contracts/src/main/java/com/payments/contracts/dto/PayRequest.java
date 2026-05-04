package com.payments.contracts.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PayRequest(
        @NotBlank String payerVpa,
        @NotBlank String payeeVpa,
        @NotNull @Min(1) Long amountPaise
) {}
