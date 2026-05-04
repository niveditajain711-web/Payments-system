package com.payments.bank.repo;

import com.payments.bank.domain.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Optional<AccountEntity> findByVpa(String vpa);
}
