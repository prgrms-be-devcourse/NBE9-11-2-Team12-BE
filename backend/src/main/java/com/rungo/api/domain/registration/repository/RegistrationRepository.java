package com.rungo.api.domain.registration.repository;

import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    @EntityGraph(attributePaths = {"marathon", "course"})
    Page<Registration> findByUser_Id(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"marathon", "course"})
    Page<Registration> findByUser_IdAndStatus(Long userId, RegistrationStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "course"})
    List<Registration> findAllByMarathon_IdOrderByAppliedAtDesc(Long marathonId);

    @Query("""
    SELECT DISTINCT r.user.email
    FROM Registration r
    WHERE r.course.marathon.id = :marathonId
    """)
    List<String> findParticipantEmailsByMarathonId(@Param("marathonId") Long marathonId);
}
