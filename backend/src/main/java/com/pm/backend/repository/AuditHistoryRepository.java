package com.pm.backend.repository;

import com.pm.backend.entity.AuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditHistoryRepository extends JpaRepository<AuditHistory, Long> {
}
