package com.rungo.api.domain.marathon.course.repository;

import com.rungo.api.domain.marathon.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByMarathon_IdOrderByIdAsc(Long marathonId);
}
