package com.payments.npci.service;

import com.payments.contracts.dto.CollectApproveRequest;
import com.payments.contracts.dto.CollectCreatedResponse;
import com.payments.contracts.dto.CollectRequest;
import com.payments.contracts.dto.PayRequest;
import com.payments.contracts.dto.PayResponse;
import com.payments.npci.domain.CollectRequestEntity;
import com.payments.npci.repo.CollectRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CollectService {

    private final CollectRequestRepository collects;
    private final PayOrchestrationService payOrchestration;

    public CollectService(CollectRequestRepository collects, PayOrchestrationService payOrchestration) {
        this.collects = collects;
        this.payOrchestration = payOrchestration;
    }

    @Transactional
    public CollectCreatedResponse create(CollectRequest req, String idempotencyKey) {
        UUID id = UUID.randomUUID();
        String npciTxnId = UUID.randomUUID().toString();
        var e = new CollectRequestEntity(id, req.payerVpa(), req.payeeVpa(), req.amountPaise(),
                CollectRequestEntity.Status.PENDING, npciTxnId, req.note());
        collects.save(e);
        return new CollectCreatedResponse(id.toString(), "PENDING", npciTxnId);
    }

    @Transactional
    public PayResponse approve(UUID collectId, CollectApproveRequest approve, String idempotencyKey) {
        var c = collects.findById(collectId).orElseThrow(() -> new IllegalArgumentException("collect not found"));
        if (c.getStatus() != CollectRequestEntity.Status.PENDING) {
            return PayResponse.failure("94", "Collect not pending");
        }
        if (approve.upiPinEncrypted() == null || approve.upiPinEncrypted().isBlank()) {
            return PayResponse.failure("17", "PIN required");
        }
        PayResponse pr = payOrchestration.pay(
                new PayRequest(c.getPayerVpa(), c.getPayeeVpa(), c.getAmountPaise()),
                idempotencyKey != null ? idempotencyKey : "collect:" + collectId
        );
        if ("SUCCESS".equals(pr.status())) {
            c.setStatus(CollectRequestEntity.Status.COMPLETED);
            c.setNpciTxnId(pr.npciTxnId());
        } else {
            c.setStatus(CollectRequestEntity.Status.FAILED);
        }
        collects.save(c);
        return pr;
    }
}
