package com.payments.tpap.api;

import com.payments.contracts.dto.VpaRegisterRequest;
import com.payments.contracts.dto.VpaRegisterResponse;
import com.payments.tpap.client.PspForwardClient;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/vpas")
public class TpapVpaController {

    private final PspForwardClient psp;

    public TpapVpaController(PspForwardClient psp) {
        this.psp = psp;
    }

    @PostMapping("/register")
    public ResponseEntity<VpaRegisterResponse> register(@Valid @RequestBody VpaRegisterRequest body) {
        var payload = Map.of("bankCode", body.bankCode(), "accountId", body.accountId());
        String path = UriComponentsBuilder.fromPath("/internal/v1/vpas").pathSegment(body.vpa()).build().toUriString();
        return psp.putJson(path, payload, VpaRegisterResponse.class);
    }
}
