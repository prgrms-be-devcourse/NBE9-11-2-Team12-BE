package com.rungo.api.domain.registration.repository;

import com.rungo.api.domain.registration.entity.Registration;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    @EntityGraph(attributePaths = {"marathon", "course"})
    List<Registration> findAllByUser_IdOrderByAppliedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"user", "course"})
    List<Registration> findAllByMarathon_IdOrderByAppliedAtDesc(Long marathonId);

    @Query("""
    SELECT DISTINCT r.user.email
    FROM Registration r
    WHERE r.course.marathon.id = :marathonId
    """)
    List<String> findParticipantEmailsByMarathonId(@Param("marathonId") Long marathonId);
}
