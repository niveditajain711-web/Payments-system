package com.payments.npci.repo;

import com.payments.npci.domain.PayIdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayIdempotencyRepository extends JpaRepository<PayIdempotencyEntity, String> {
}
