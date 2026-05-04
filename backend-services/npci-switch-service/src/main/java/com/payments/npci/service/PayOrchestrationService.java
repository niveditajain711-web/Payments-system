package com.payments.npci.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payments.contracts.dto.PayRequest;
import com.payments.contracts.dto.PayResponse;
import com.payments.contracts.dto.internal.BankJournalApplyRequest;
import com.payments.contracts.dto.internal.BankJournalApplyResponse;
import com.payments.npci.client.BankRailClient;
import com.payments.npci.config.NpciProperties;
import com.payments.npci.domain.AuditLogEntity;
import com.payments.npci.domain.PayIdempotencyEntity;
import com.payments.npci.domain.VpaDirectoryEntity;
import com.payments.npci.repo.AuditLogRepository;
import com.payments.npci.repo.PayIdempotencyRepository;
import com.payments.npci.repo.VpaDirectoryRepository;
import com.payments.npci.routing.DemoRouting;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PayOrchestrationService {

    private final VpaDirectoryRepository vpaDirectory;
    private final NpciProperties props;
    private final BankRailClient bankRail;
    private final AuditLogRepository auditLog;
    private final PayIdempotencyRepository payIdempotency;
    private final ObjectMapper objectMapper;

    public PayOrchestrationService(VpaDirectoryRepository vpaDirectory, NpciProperties props, BankRailClient bankRail,
                                 AuditLogRepository auditLog, PayIdempotencyRepository payIdempotency, ObjectMapper objectMapper) {
        this.vpaDirectory = vpaDirectory;
        this.props = props;
        this.bankRail = bankRail;
        this.auditLog = auditLog;
        this.payIdempotency = payIdempotency;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PayResponse pay(PayRequest req, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var prev = payIdempotency.findById(idempotencyKey);
            if (prev.isPresent()) {
                try {
                    return objectMapper.readValue(prev.get().getResponseJson(), PayResponse.class);
                } catch (Exception e) {
                    throw new IllegalStateException("Corrupt idempotency payload for pay", e);
                }
            }
        }

        var payerRow = vpaDirectory.findById(req.payerVpa());
        var payeeRow = vpaDirectory.findById(req.payeeVpa());
        if (payerRow.isEmpty() || payeeRow.isEmpty()) {
            return finish(idempotencyKey, null, PayResponse.failure("12", "Unknown VPA"));
        }

        VpaDirectoryEntity payer = payerRow.get();
        VpaDirectoryEntity payee = payeeRow.get();
        String npciTxnId = UUID.randomUUID().toString();
        long amt = req.amountPaise();

        PayResponse result;
        if (payer.getBankCode().equalsIgnoreCase(payee.getBankCode())) {
            result = sameBankPay(npciTxnId, idempotencyKey, payer, payee, amt);
        } else {
            result = crossBankPay(npciTxnId, idempotencyKey, payer, payee, amt);
        }

        auditLog.save(new AuditLogEntity(UUID.randomUUID(), npciTxnId, MDC.get("correlationId"), "PAY",
                req.payerVpa() + "->" + req.payeeVpa(), result.npciResponseCode()));

        return finish(idempotencyKey, npciTxnId, result);
    }

    private PayResponse sameBankPay(String npciTxnId, String idem, VpaDirectoryEntity payer, VpaDirectoryEntity payee, long amt) {
        String base = baseUrl(payer.getBankCode());
        var applyReq = new BankJournalApplyRequest(npciTxnId, idem + ":same", List.of(
                new BankJournalApplyRequest.LedgerLineDto(payer.getAccountId().toString(), amt, 0),
                new BankJournalApplyRequest.LedgerLineDto(payee.getAccountId().toString(), 0, amt)
        ));
        BankJournalApplyResponse r = bankRail.applyJournal(base, applyReq, idem + ":same");
        return toPayResponse(npciTxnId, r);
    }

    private PayResponse crossBankPay(String npciTxnId, String idem, VpaDirectoryEntity payer, VpaDirectoryEntity payee, long amt) {
        String baseA = baseUrl(payer.getBankCode());
        String baseB = baseUrl(payee.getBankCode());
        UUID clearingPayer = "A".equalsIgnoreCase(payer.getBankCode()) ? DemoRouting.CLEARING_A : DemoRouting.CLEARING_B;
        UUID clearingPayee = "A".equalsIgnoreCase(payee.getBankCode()) ? DemoRouting.CLEARING_A : DemoRouting.CLEARING_B;

        var legA = new BankJournalApplyRequest(npciTxnId, idem + ":xa", List.of(
                new BankJournalApplyRequest.LedgerLineDto(payer.getAccountId().toString(), amt, 0),
                new BankJournalApplyRequest.LedgerLineDto(clearingPayer.toString(), 0, amt)
        ));
        BankJournalApplyResponse ra = bankRail.applyJournal(baseA, legA, idem + ":xa");
        if (!"SUCCESS".equals(ra.status())) {
            return toPayResponse(npciTxnId, ra);
        }

        var legB = new BankJournalApplyRequest(npciTxnId, idem + ":xb", List.of(
                new BankJournalApplyRequest.LedgerLineDto(clearingPayee.toString(), amt, 0),
                new BankJournalApplyRequest.LedgerLineDto(payee.getAccountId().toString(), 0, amt)
        ));
        BankJournalApplyResponse rb = bankRail.applyJournal(baseB, legB, idem + ":xb");
        if (!"SUCCESS".equals(rb.status())) {
            var compensate = new BankJournalApplyRequest(npciTxnId, idem + ":xcomp", List.of(
                    new BankJournalApplyRequest.LedgerLineDto(clearingPayer.toString(), amt, 0),
                    new BankJournalApplyRequest.LedgerLineDto(payer.getAccountId().toString(), 0, amt)
            ));
            bankRail.applyJournal(baseA, compensate, idem + ":xcomp");
            return PayResponse.failure(rb.npciResponseCode() != null ? rb.npciResponseCode() : "96", "Settlement failed; payer debits reversed");
        }

        return PayResponse.success(npciTxnId);
    }

    private PayResponse toPayResponse(String npciTxnId, BankJournalApplyResponse r) {
        if ("SUCCESS".equals(r.status())) {
            return PayResponse.success(npciTxnId);
        }
        return PayResponse.failure(r.npciResponseCode() != null ? r.npciResponseCode() : "96",
                r.message() != null ? r.message() : "Bank rejected");
    }

    private PayResponse finish(String idempotencyKey, String npciTxnId, PayResponse r) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                String json = objectMapper.writeValueAsString(r);
                payIdempotency.save(new PayIdempotencyEntity(idempotencyKey, npciTxnId != null ? npciTxnId : "", json));
            } catch (Exception e) {
                throw new IllegalStateException("Failed to persist pay idempotency", e);
            }
        }
        return r;
    }

    private String baseUrl(String bankCode) {
        if ("A".equalsIgnoreCase(bankCode)) {
            return props.bankABaseUrl();
        }
        if ("B".equalsIgnoreCase(bankCode)) {
            return props.bankBBaseUrl();
        }
        throw new IllegalArgumentException("Unknown bank " + bankCode);
    }
}
