package com.payments.contracts.dto;

public record CollectCreatedResponse(
        String collectId,
        String status,
        String npciTxnId
) {}
