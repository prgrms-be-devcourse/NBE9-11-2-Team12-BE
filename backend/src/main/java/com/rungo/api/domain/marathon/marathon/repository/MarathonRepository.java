package com.rungo.api.domain.marathon.marathon.repository;

import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarathonRepository extends JpaRepository<Marathon, Long> {
}
