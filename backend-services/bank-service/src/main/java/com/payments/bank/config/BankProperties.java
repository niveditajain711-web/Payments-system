package com.payments.bank.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payments.bank")
public record BankProperties(
        String code
) {
    public BankProperties {
        if (code == null || code.isBlank()) {
            code = "A";
        }
    }
}
