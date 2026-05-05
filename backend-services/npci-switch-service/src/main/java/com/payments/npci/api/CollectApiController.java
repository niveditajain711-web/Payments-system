package com.payments.npci.api;

import com.payments.contracts.dto.CollectApproveRequest;
import com.payments.contracts.dto.CollectCreatedResponse;
import com.payments.contracts.dto.CollectRequest;
import com.payments.contracts.dto.PayResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.contracts.http.HttpHeaders;
import com.payments.npci.service.CollectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.NPCI_BASE)
public class CollectApiController {

    private final CollectService collectService;

    public CollectApiController(CollectService collectService) {
        this.collectService = collectService;
    }

    @PostMapping(ApiPaths.COLLECT_REQUESTS)
    public CollectCreatedResponse create(@RequestHeader(value = HttpHeaders.IDEMPOTENCY_KEY, required = false) String idem,
                                         @Valid @RequestBody CollectRequest body) {
        return collectService.create(body, idem);
    }

    @PostMapping(ApiPaths.COLLECT_APPROVE)
    public ResponseEntity<PayResponse> approve(@PathVariable String id,
                                                 @RequestHeader(value = HttpHeaders.IDEMPOTENCY_KEY, required = false) String idem,
                                                 @Valid @RequestBody CollectApproveRequest body) {
        try {
            PayResponse r = collectService.approve(UUID.fromString(id), body, idem);
            if ("SUCCESS".equals(r.status())) {
                return ResponseEntity.ok(r);
            }
            return ResponseEntity.status(400).body(r);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(PayResponse.failure("17", e.getMessage()));
        }
    }
}
