package com.payments.contracts.http;

public final class HttpHeaders {
    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String IDEMPOTENCY_KEY = "Idempotency-Key";

    private HttpHeaders() {}
}
