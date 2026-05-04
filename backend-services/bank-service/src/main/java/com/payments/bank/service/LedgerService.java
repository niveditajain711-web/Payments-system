package com.payments.bank.service;

import com.payments.bank.domain.AccountEntity;
import com.payments.bank.domain.IdempotencyEntity;
import com.payments.bank.domain.LedgerLineEntity;
import com.payments.bank.repo.AccountRepository;
import com.payments.bank.repo.IdempotencyRepository;
import com.payments.bank.repo.LedgerLineRepository;
import com.payments.contracts.dto.internal.BankJournalApplyRequest;
import com.payments.contracts.dto.internal.BankJournalApplyResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LedgerService {

    public static final String INSUFFICIENT_FUNDS = "51";

    private final AccountRepository accounts;
    private final LedgerLineRepository ledgerLines;
    private final IdempotencyRepository idempotency;

    public LedgerService(AccountRepository accounts, LedgerLineRepository ledgerLines, IdempotencyRepository idempotency) {
        this.accounts = accounts;
        this.ledgerLines = ledgerLines;
        this.idempotency = idempotency;
    }

    @Transactional
    public BankJournalApplyResponse apply(BankJournalApplyRequest req) {
        String idem = req.idempotencyKey();
        if (idem != null && !idem.isBlank()) {
            var existing = idempotency.findById(idem);
            if (existing.isPresent()) {
                var e = existing.get();
                return new BankJournalApplyResponse(e.getResponseStatus(), e.getResponseCode(), e.getResponseMessage());
            }
        }

        List<BankJournalApplyRequest.LedgerLineDto> lines = req.lines();
        long dr = lines.stream().mapToLong(BankJournalApplyRequest.LedgerLineDto::debitPaise).sum();
        long cr = lines.stream().mapToLong(BankJournalApplyRequest.LedgerLineDto::creditPaise).sum();
        if (dr != cr) {
            return persistIdem(idem, req.npciTxnId(), BankJournalApplyResponse.fail("96", "Unbalanced journal"));
        }

        List<AccountEntity> loaded = new ArrayList<>();
        for (var dto : lines) {
            UUID aid = UUID.fromString(dto.accountId());
            AccountEntity acc = accounts.findById(aid).orElse(null);
            if (acc == null) {
                return persistIdem(idem, req.npciTxnId(), BankJournalApplyResponse.fail("12", "Invalid account"));
            }
            loaded.add(acc);
        }

        for (int i = 0; i < lines.size(); i++) {
            var dto = lines.get(i);
            AccountEntity acc = loaded.get(i);
            long delta = dto.creditPaise() - dto.debitPaise();
            long newBal = acc.getBalancePaise() + delta;
            if (acc.getKind() == AccountEntity.Kind.USER && newBal < 0) {
                return persistIdem(idem, req.npciTxnId(), BankJournalApplyResponse.fail(INSUFFICIENT_FUNDS, "Insufficient funds"));
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            var dto = lines.get(i);
            AccountEntity acc = loaded.get(i);
            long delta = dto.creditPaise() - dto.debitPaise();
            acc.setBalancePaise(acc.getBalancePaise() + delta);
            accounts.save(acc);
            ledgerLines.save(new LedgerLineEntity(UUID.randomUUID(), req.npciTxnId(), acc, dto.debitPaise(), dto.creditPaise()));
        }

        var ok = BankJournalApplyResponse.ok();
        persistIdem(idem, req.npciTxnId(), ok);
        return ok;
    }

    private BankJournalApplyResponse persistIdem(String idem, String txnId, BankJournalApplyResponse r) {
        if (idem != null && !idem.isBlank()) {
            idempotency.save(new IdempotencyEntity(idem, txnId == null ? "" : txnId, r.status(), r.npciResponseCode(), r.message()));
        }
        return r;
    }
}
