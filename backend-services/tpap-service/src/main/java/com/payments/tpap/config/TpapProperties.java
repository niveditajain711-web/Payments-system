package com.payments.tpap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payments.tpap")
public record TpapProperties(
        String pspBaseUrl
) {
    public TpapProperties {
        if (pspBaseUrl == null || pspBaseUrl.isBlank()) {
            pspBaseUrl = "http://localhost:8081";
        }
    }
}
