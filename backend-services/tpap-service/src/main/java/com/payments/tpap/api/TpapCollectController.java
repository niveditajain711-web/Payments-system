package com.payments.tpap.api;

import com.payments.contracts.dto.CollectApproveRequest;
import com.payments.contracts.dto.CollectCreatedResponse;
import com.payments.contracts.dto.CollectRequest;
import com.payments.contracts.dto.PayResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.contracts.http.HttpHeaders;
import com.payments.tpap.client.PspForwardClient;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.TPAP_BASE)
public class TpapCollectController {

    private final PspForwardClient psp;

    public TpapCollectController(PspForwardClient psp) {
        this.psp = psp;
    }

    @PostMapping(ApiPaths.COLLECT_REQUESTS)
    public ResponseEntity<CollectCreatedResponse> create(
            @RequestHeader(value = HttpHeaders.IDEMPOTENCY_KEY, required = false) String idem,
            @Valid @RequestBody CollectRequest body) {
        return psp.createCollect(body, idem);
    }

    @PostMapping(ApiPaths.COLLECT_APPROVE)
    public ResponseEntity<PayResponse> approve(
            @PathVariable("id") String id,
            @RequestHeader(value = HttpHeaders.IDEMPOTENCY_KEY, required = false) String idem,
            @Valid @RequestBody CollectApproveRequest body) {
        return psp.approveCollect(id, body, idem);
    }
}
