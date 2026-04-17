package com.rungo.api.domain.marathon.marathon.repository;

import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarathonRepository extends JpaRepository<Marathon, Long> {
    Page<Marathon> findByStatusIn(List<MarathonStatus> statuses, Pageable pageable);
}
