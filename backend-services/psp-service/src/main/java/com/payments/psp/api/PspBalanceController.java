package com.payments.psp.api;

import com.payments.contracts.dto.BalanceResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.psp.client.NpciForwardClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.PSP_BASE)
public class PspBalanceController {

    private final NpciForwardClient npci;

    public PspBalanceController(NpciForwardClient npci) {
        this.npci = npci;
    }

    @GetMapping(ApiPaths.BALANCE)
    public ResponseEntity<BalanceResponse> balance(@RequestParam String vpa) {
        return npci.getBalance(vpa);
    }
}
