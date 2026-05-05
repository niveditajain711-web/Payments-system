package com.payments.bank.api;

import com.payments.bank.domain.AccountEntity;
import com.payments.bank.repo.AccountRepository;
import com.payments.contracts.dto.BalanceResponse;
import com.payments.contracts.http.ApiPaths;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.BANK_BASE)
public class InternalBalanceController {

    private final AccountRepository accounts;

    public InternalBalanceController(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @GetMapping(ApiPaths.BALANCE)
    public ResponseEntity<BalanceResponse> balance(@RequestParam String vpa) {
        return accounts.findByVpa(vpa)
                .filter(a -> a.getKind() == AccountEntity.Kind.USER)
                .map(a -> ResponseEntity.ok(new BalanceResponse(a.getVpa(), a.getBalancePaise())))
                .orElse(ResponseEntity.notFound().build());
    }
}
