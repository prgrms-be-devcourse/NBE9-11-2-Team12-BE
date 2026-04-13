package com.rungo.api.domain.application.entity;

import com.rungo.api.domain.application.enumtype.ApplicationStatus;
import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
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
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_application_user_marathon", columnNames = {"user_id", "marathon_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 신청한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    /**
     * 신청한 코스
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * 중복 신청 방지용/조회 편의용
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marathon_id", nullable = false)
    private Marathon marathon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status;

    /**
     * 신청 당시 스냅샷
     */
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

    /**
     *  취소 시간은 직접 세팅
     */
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "agreed_terms", nullable = false)
    private boolean agreedTerms;

}