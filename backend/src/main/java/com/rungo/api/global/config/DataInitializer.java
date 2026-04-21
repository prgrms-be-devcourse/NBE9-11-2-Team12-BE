package com.rungo.api.global.config;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Gender;
import com.rungo.api.domain.users.enumtype.Role;
import com.rungo.api.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final MarathonRepository marathonRepository;
    private final RegistrationRepository registrationRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initTestData() {
        return args -> {

            // organizer 1명
            Users organizer = userRepository.findByEmail("organizer@test.com")
                                            .orElseGet(() -> userRepository.save(
                                                    Users.builder()
                                                         .email("organizer@test.com")
                                                         .password(passwordEncoder.encode("Password123!"))
                                                         .name("주최자")
                                                         .phoneNumber("010-2222-2222")
                                                         .role(Role.ORGANIZER)
                                                         .gender(Gender.MALE)
                                                         .birth(LocalDate.of(2000, 1, 1))
                                                         .build()
                                            ));

            // participant 1002명
            for (int i = 1; i <= 10; i++) {
                String email = "user" + i + "@test.com";

                if (userRepository.findByEmail(email).isEmpty()) {
                    userRepository.save(
                            Users.builder()
                                 .email(email)
                                 .password(passwordEncoder.encode("Password123!"))
                                 .name("참가자" + i)
                                 .phoneNumber(String.format("010-%04d-%04d", i / 10000, i % 10000))
                                 .role(Role.PARTICIPANT)
                                 .gender(Gender.MALE)
                                 .birth(LocalDate.of(2000, 1, 1))
                                 .build()
                    );
                }
            }

            // 참가 신청 성능 테스트용 마라톤 1개
            boolean performanceMarathonExists = marathonRepository.findAll().stream()
                                                                  .anyMatch(m -> "테스트용 마라톤".equals(m.getTitle()));

            if (!performanceMarathonExists) {
                Marathon marathon = new Marathon(
                        organizer,
                        "테스트용 마라톤",
                        "서울",
                        "성동구",
                        LocalDate.now().plusDays(30),
                        "poster.png",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(10),
                        MarathonStatus.OPEN
                );

                Course course = new Course(
                        "10K",
                        BigDecimal.valueOf(30000),
                        15000,
                        0
                );

                marathon.addCourse(course);
                marathonRepository.save(marathon);
            }

            // 대회 취소 테스트용 마라톤 100개 + 각 마라톤별 참가 신청 1건
            for (int i = 1; i <= 10; i++) {
                final int index = i;
                String marathonTitle = "취소테스트 마라톤 " + i;

                boolean cancelMarathonExists = marathonRepository.findAll().stream()
                                                                 .anyMatch(m -> marathonTitle.equals(m.getTitle()));

                if (cancelMarathonExists) {
                    continue;
                }

                Marathon cancelMarathon = new Marathon(
                        organizer,
                        marathonTitle,
                        "서울",
                        "성동구",
                        LocalDate.now().plusDays(30),
                        "poster.png",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(10),
                        MarathonStatus.OPEN
                );

                Course cancelCourse = new Course(
                        "10K",
                        BigDecimal.valueOf(30000),
                        15000,
                        0
                );

                cancelMarathon.addCourse(cancelCourse);
                Marathon savedMarathon = marathonRepository.save(cancelMarathon);

                Users participant = userRepository.findByEmail("user" + index + "@test.com")
                                                  .orElseThrow(() -> new IllegalStateException("취소 테스트용 참가자 없음: user" + index + "@test.com"));

                Course savedCourse = savedMarathon.getCourses().get(0);

                registrationRepository.save(
                        Registration.create(
                                participant,
                                savedCourse,
                                savedMarathon,
                                "12345",
                                "서울시 강남구",
                                "101동",
                                "L",
                                true
                        )
                );
            }

            System.out.println("테스트 유저 / 마라톤 / 코스 / 취소 테스트 데이터 생성 완료");
            System.out.println("organizer: organizer@test.com / Password123!");
            System.out.println("participants: user1@test.com ~ user1002@test.com / Password123!");
        };
    }
}