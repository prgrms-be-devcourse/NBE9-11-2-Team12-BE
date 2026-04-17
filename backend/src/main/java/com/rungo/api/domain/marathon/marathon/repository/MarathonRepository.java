package com.rungo.api.domain.marathon.marathon.repository;

import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MarathonRepository extends JpaRepository<Marathon, Long> {
    Page<Marathon> findByStatusIn(List<MarathonStatus> statuses, Pageable pageable);
    @Query("""
    SELECT DISTINCT m
    FROM Marathon m
    LEFT JOIN FETCH m.courses
    WHERE m.id = :marathonId
      AND m.organizer.id = :organizerId
""")
    Optional<Marathon> findByIdAndOrganizerIdWithCourses(Long marathonId, Long organizerId);
}
