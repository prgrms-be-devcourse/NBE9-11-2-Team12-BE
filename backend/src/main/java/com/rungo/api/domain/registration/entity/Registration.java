package com.rungo.api.domain.registration.entity;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;
import com.rungo.api.domain.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "registrations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_registration_user_marathon", columnNames = {"user_id", "marathon_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marathon_id", nullable = false)
    private Marathon marathon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegistrationStatus status;

    @Column(name = "snap_name", nullable = false, length = 50)
    private String snapName;

    @Column(name = "snap_phone_number", nullable = false, length = 20)
    private String snapPhoneNumber;

    @Column(name = "snap_zip_code", nullable = false, length = 10)
    private String snapZipCode;

    @Column(name = "snap_address", nullable = false, length = 255)
    private String snapAddress;

    @Column(name = "snap_detail", length = 255)
    private String snapDetail;

    @Column(name = "t_size", nullable = false, length = 10)
    private String tSize;

    @Column(name = "applied_at", nullable = false)
    @CreatedDate
    private LocalDateTime appliedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "agreed_terms", nullable = false)
    private boolean agreedTerms;
}
