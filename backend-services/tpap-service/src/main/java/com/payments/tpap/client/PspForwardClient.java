package com.payments.tpap.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.contracts.http.HttpHeaders;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PspForwardClient {

    private final RestClient psp;
    private final ObjectMapper mapper;

    public PspForwardClient(RestClient pspRestClient, ObjectMapper mapper) {
        this.psp = pspRestClient;
        this.mapper = mapper;
    }

    public <T> ResponseEntity<T> postJson(String path, Object body, String idempotencyKey, Class<T> responseType) {
        String cid = MDC.get("correlationId");
        return psp.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> {
                    if (cid != null && !cid.isBlank()) {
                        h.set(HttpHeaders.CORRELATION_ID, cid);
                    }
                    if (idempotencyKey != null && !idempotencyKey.isBlank()) {
                        h.set(HttpHeaders.IDEMPOTENCY_KEY, idempotencyKey);
                    }
                })
                .body(body)
                .exchange((request, response) -> parseEntity(response, responseType));
    }

    public <T> ResponseEntity<T> getJson(String path, Class<T> responseType) {
        String cid = MDC.get("correlationId");
        return psp.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> {
                    if (cid != null && !cid.isBlank()) {
                        h.set(HttpHeaders.CORRELATION_ID, cid);
                    }
                })
                .exchange((request, response) -> parseEntity(response, responseType));
    }

    public <T> ResponseEntity<T> putJson(String path, Object body, Class<T> responseType) {
        String cid = MDC.get("correlationId");
        return psp.put()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> {
                    if (cid != null && !cid.isBlank()) {
                        h.set(HttpHeaders.CORRELATION_ID, cid);
                    }
                })
                .body(body)
                .exchange((request, response) -> parseEntity(response, responseType));
    }

    private <T> ResponseEntity<T> parseEntity(org.springframework.http.client.ClientHttpResponse response, Class<T> type)
            throws java.io.IOException {
        byte[] raw = response.getBody().readAllBytes();
        if (raw.length == 0) {
            return ResponseEntity.status(response.getStatusCode()).build();
        }
        T parsed = mapper.readValue(raw, type);
        return ResponseEntity.status(response.getStatusCode()).body(parsed);
    }
}
