package com.payments.psp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payments.psp")
public record PspProperties(
        /** Base URL of npci-switch-service (no trailing slash). */
        String npciBaseUrl
) {
    public PspProperties {
        if (npciBaseUrl == null || npciBaseUrl.isBlank()) {
            npciBaseUrl = "http://localhost:8082";
        }
    }
}
