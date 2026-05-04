package com.payments.tpap.api;

import com.payments.contracts.dto.BalanceResponse;
import com.payments.tpap.client.PspForwardClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1")
public class TpapBalanceController {

    private final PspForwardClient psp;

    public TpapBalanceController(PspForwardClient psp) {
        this.psp = psp;
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> balance(@RequestParam String vpa) {
        String path = UriComponentsBuilder.fromPath("/api/v1/balance").queryParam("vpa", vpa).build().toUriString();
        return psp.getJson(path, BalanceResponse.class);
    }
}
