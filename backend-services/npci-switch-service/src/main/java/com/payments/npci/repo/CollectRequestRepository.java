package com.payments.npci.repo;

import com.payments.npci.domain.CollectRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CollectRequestRepository extends JpaRepository<CollectRequestEntity, UUID> {
}
