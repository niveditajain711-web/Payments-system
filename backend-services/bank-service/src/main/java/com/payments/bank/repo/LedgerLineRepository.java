package com.payments.bank.repo;

import com.payments.bank.domain.LedgerLineEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerLineRepository extends JpaRepository<LedgerLineEntity, UUID> {
}
