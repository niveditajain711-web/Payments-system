package com.payments.tpap.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.contracts.dto.BalanceResponse;
import com.payments.contracts.dto.CollectApproveRequest;
import com.payments.contracts.dto.CollectCreatedResponse;
import com.payments.contracts.dto.CollectRequest;
import com.payments.contracts.dto.PayRequest;
import com.payments.contracts.dto.PayResponse;
import com.payments.contracts.dto.VpaRegisterResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.contracts.http.HttpHeaders;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class PspForwardClient {

    private final RestClient psp;
    private final ObjectMapper mapper;

    public PspForwardClient(RestClient pspRestClient, ObjectMapper mapper) {
        this.psp = pspRestClient;
        this.mapper = mapper;
    }

    public ResponseEntity<PayResponse> pay(PayRequest body, String idempotencyKey) {
        return postWithIdempotency(ApiPaths.PSP_BASE + ApiPaths.PAY, body, idempotencyKey, PayResponse.class);
    }

    public ResponseEntity<CollectCreatedResponse> createCollect(CollectRequest body, String idempotencyKey) {
        return postWithIdempotency(ApiPaths.PSP_BASE + ApiPaths.COLLECT_REQUESTS, body, idempotencyKey, CollectCreatedResponse.class);
    }

    public ResponseEntity<PayResponse> approveCollect(String id, CollectApproveRequest body, String idempotencyKey) {
        String path = ApiPaths.PSP_BASE + ApiPaths.COLLECT_APPROVE.replace("{id}", id);
        return postWithIdempotency(path, body, idempotencyKey, PayResponse.class);
    }

    public ResponseEntity<BalanceResponse> getBalance(String vpa) {
        String path = UriComponentsBuilder.fromPath(ApiPaths.PSP_BASE + ApiPaths.BALANCE).queryParam("vpa", vpa).build().toUriString();
        return get(path, BalanceResponse.class);
    }

    public ResponseEntity<VpaRegisterResponse> registerVpa(String vpaId, String bankCode, String accountId) {
        String path = UriComponentsBuilder.fromPath(ApiPaths.PSP_BASE + ApiPaths.REGISTER_VPA_WITH_ID).buildAndExpand(vpaId).toUriString();
        return put(path, Map.of("bankCode", bankCode, "accountId", accountId), VpaRegisterResponse.class);
    }

    private <T> ResponseEntity<T> postWithIdempotency(String path, Object body, String idempotencyKey, Class<T> responseType) {
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

    private <T> ResponseEntity<T> get(String path, Class<T> responseType) {
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

    private <T> ResponseEntity<T> put(String path, Object body, Class<T> responseType) {
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
