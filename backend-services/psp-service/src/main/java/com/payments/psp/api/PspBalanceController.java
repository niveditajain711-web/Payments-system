package com.payments.psp.api;

import com.payments.contracts.dto.BalanceResponse;
import com.payments.psp.client.NpciForwardClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1")
public class PspBalanceController {

    private final NpciForwardClient npci;

    public PspBalanceController(NpciForwardClient npci) {
        this.npci = npci;
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> balance(@RequestParam String vpa) {
        String path = UriComponentsBuilder.fromPath("/api/v1/balance").queryParam("vpa", vpa).build().toUriString();
        return npci.getJson(path, BalanceResponse.class);
    }
}
