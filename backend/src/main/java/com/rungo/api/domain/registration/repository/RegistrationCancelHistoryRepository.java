package com.rungo.api.domain.registration.repository;

import com.rungo.api.domain.registration.entity.RegistrationCancelHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationCancelHistoryRepository extends JpaRepository<RegistrationCancelHistory, Long> {
}
