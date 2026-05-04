package com.payments.npci.repo;

import com.payments.npci.domain.VpaDirectoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VpaDirectoryRepository extends JpaRepository<VpaDirectoryEntity, String> {
}
