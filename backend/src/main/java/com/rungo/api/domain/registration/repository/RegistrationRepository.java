package com.rungo.api.domain.registration.repository;

import com.rungo.api.domain.registration.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
}
