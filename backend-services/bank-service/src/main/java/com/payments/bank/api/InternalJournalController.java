package com.payments.bank.api;

import com.payments.bank.service.LedgerService;
import com.payments.contracts.dto.internal.BankJournalApplyRequest;
import com.payments.contracts.dto.internal.BankJournalApplyResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/v1/journals")
public class InternalJournalController {

    private final LedgerService ledgerService;

    public InternalJournalController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    /**
     * Always returns 200 with a body so downstream RestClient calls do not throw on business failures
     * (educational simplification; a gateway can map codes later).
     */
    @PostMapping("/apply")
    public BankJournalApplyResponse apply(@Valid @RequestBody BankJournalApplyRequest body) {
        return ledgerService.apply(body);
    }
}
