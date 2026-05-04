package com.payments.bank.repo;

import com.payments.bank.domain.IdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyRepository extends JpaRepository<IdempotencyEntity, String> {
}
