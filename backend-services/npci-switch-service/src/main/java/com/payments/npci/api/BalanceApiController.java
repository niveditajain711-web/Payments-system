package com.payments.npci.api;

import com.payments.contracts.dto.BalanceResponse;
import com.payments.npci.service.BalanceQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BalanceApiController {

    private final BalanceQueryService balanceQueryService;

    public BalanceApiController(BalanceQueryService balanceQueryService) {
        this.balanceQueryService = balanceQueryService;
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> balance(@RequestParam String vpa) {
        try {
            return ResponseEntity.ok(balanceQueryService.balance(vpa));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
