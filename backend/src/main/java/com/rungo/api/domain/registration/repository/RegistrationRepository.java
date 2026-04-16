package com.rungo.api.domain.registration.repository;

import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findAllByUser_IdOrderByAppliedAtDesc(Long userId);
    }
