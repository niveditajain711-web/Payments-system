package com.payments.npci.api;

import com.payments.contracts.dto.PayRequest;
import com.payments.contracts.dto.PayResponse;
import com.payments.contracts.http.HttpHeaders;
import com.payments.npci.service.PayOrchestrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PaymentApiController {

    private final PayOrchestrationService payOrchestration;

    public PaymentApiController(PayOrchestrationService payOrchestration) {
        this.payOrchestration = payOrchestration;
    }

    @PostMapping("/payments/pay")
    public ResponseEntity<PayResponse> pay(HttpServletRequest http,
                                           @RequestHeader(value = HttpHeaders.IDEMPOTENCY_KEY, required = false) String idem,
                                           @Valid @RequestBody PayRequest body) {
        try {
            PayResponse r = payOrchestration.pay(body, idem);
            if ("SUCCESS".equals(r.status())) {
                return ResponseEntity.ok(r);
            }
            return ResponseEntity.status(400).body(r);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(PayResponse.failure("96", e.getMessage()));
        }
    }
}
