package com.payments.npci.client;

import com.payments.contracts.dto.internal.BankJournalApplyRequest;
import com.payments.contracts.dto.internal.BankJournalApplyResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.contracts.http.HttpHeaders;
import org.slf4j.MDC;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BankRailClient {

    private final JdkClientHttpRequestFactory requestFactory;

    public BankRailClient(JdkClientHttpRequestFactory requestFactory) {
        this.requestFactory = requestFactory;
    }

    public BankJournalApplyResponse applyJournal(String bankBaseUrl, BankJournalApplyRequest request, String idempotencyKey) {
        String cid = MDC.get("correlationId");
        RestClient client = RestClient.builder()
                .baseUrl(bankBaseUrl)
                .requestFactory(requestFactory)
                .build();
        var spec = client.post()
                .uri(ApiPaths.BANK_BASE + ApiPaths.JOURNALS_APPLY)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        if (cid != null && !cid.isBlank()) {
            spec = spec.header(HttpHeaders.CORRELATION_ID, cid);
        }
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            spec = spec.header(HttpHeaders.IDEMPOTENCY_KEY, idempotencyKey);
        }
        return spec.body(request).retrieve().body(BankJournalApplyResponse.class);
    }
}
