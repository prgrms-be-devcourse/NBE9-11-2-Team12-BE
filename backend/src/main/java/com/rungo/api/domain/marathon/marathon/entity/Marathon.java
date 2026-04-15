package com.rungo.api.domain.marathon.marathon.entity;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "Marathon",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_marathon_organizerId_title_eventDate",
                        columnNames = {"organizer_id","title","event_date"})
        }

)
public class Marathon {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private Users organizer;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 50)
    private String region;

    @Column(name="event_date", nullable = false)
    private LocalDate eventDate;

    @Column(length = 500)
    private String posterImageUrl;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "registration_start_at", nullable = false)
    private LocalDateTime registrationStartAt;

    @Column(name = "registration_end_at", nullable = false)
    private LocalDateTime registrationEndAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarathonStatus status;

    @OneToMany(mappedBy = "marathon", cascade =  CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    public Marathon(
            Users organizer,
            String title,
            String region,
            LocalDate eventDate,
            String posterImageUrl,
            LocalDateTime registrationStartAt,
            LocalDateTime registrationEndAt,
            MarathonStatus status
    ) {
        this.organizer = organizer;
        this.title = title;
        this.region = region;
        this.eventDate = eventDate;
        this.posterImageUrl = posterImageUrl;
        this.registrationStartAt = registrationStartAt;
        this.registrationEndAt = registrationEndAt;
        this.status = status;
    }
    public void addCourse(Course course){
        this.courses.add(course);
        course.setMarathon(this);
    }
}
