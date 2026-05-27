package com.payments.tpap.api;

import com.payments.contracts.dto.BalanceResponse;
import com.payments.contracts.http.ApiPaths;
import com.payments.tpap.client.PspForwardClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.TPAP_BASE)
public class TpapBalanceController {

    private final PspForwardClient psp;

    public TpapBalanceController(PspForwardClient psp) {
        this.psp = psp;
    }

    @GetMapping(ApiPaths.BALANCE)
    public ResponseEntity<BalanceResponse> balance(@RequestParam("vpa") String vpa) {
        return psp.getBalance(vpa);
    }
}
