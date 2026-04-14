package com.rungo.api.domain.marathon.course.repository;

import com.rungo.api.domain.marathon.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
