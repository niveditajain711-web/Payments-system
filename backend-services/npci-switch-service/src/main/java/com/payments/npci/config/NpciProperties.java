package com.payments.npci.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payments.npci")
public record NpciProperties(
        String bankABaseUrl,
        String bankBBaseUrl
) {}
