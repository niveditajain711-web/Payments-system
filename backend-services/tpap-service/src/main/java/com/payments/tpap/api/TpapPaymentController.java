package com.payments.tpap.api;

import com.payments.contracts.dto.PayRequest;
import com.payments.contracts.dto.PayResponse;
import com.payments.contracts.http.HttpHeaders;
import com.payments.tpap.client.PspForwardClient;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TpapPaymentController {

    private final PspForwardClient psp;

    public TpapPaymentController(PspForwardClient psp) {
        this.psp = psp;
    }

    @PostMapping("/payments/pay")
    public ResponseEntity<PayResponse> pay(
            @RequestHeader(value = HttpHeaders.IDEMPOTENCY_KEY, required = false) String idem,
            @Valid @RequestBody PayRequest body) {
        return psp.postJson("/api/v1/payments/pay", body, idem, PayResponse.class);
    }
}
