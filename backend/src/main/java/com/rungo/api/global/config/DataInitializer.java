package com.rungo.api.global.config;

import com.rungo.api.domain.marathon.course.entity.Course;
import com.rungo.api.domain.marathon.marathon.entity.Marathon;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Gender;
import com.rungo.api.domain.users.enumtype.Role;
import com.rungo.api.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final MarathonRepository marathonRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initTestUsers() {
        return args -> {

            Users participant = userRepository.findByEmail("user@test.com")
                                              .orElseGet(() -> userRepository.save(
                                                      Users.builder()
                                                           .email("user@test.com")
                                                           .password(passwordEncoder.encode("Password123!"))
                                                           .name("일반유저")
                                                           .phoneNumber("010-1111-1111")
                                                           .role(Role.PARTICIPANT)
                                                           .gender(Gender.MALE)
                                                           .birth(LocalDate.of(2000, 1, 1))
                                                           .build()
                                              ));

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

            boolean marathonExists = marathonRepository.findAll().stream()
                                                       .anyMatch(m -> "테스트용 마라톤".equals(m.getTitle()));

            if (!marathonExists) {
                Marathon marathon = new Marathon(
                        organizer,
                        "테스트용 마라톤",
                        "서울",
                        LocalDate.now().plusDays(30),
                        "poster.png",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now().plusDays(10),
                        MarathonStatus.OPEN
                );

                Course course = new Course(
                        "10K",
                        BigDecimal.valueOf(30000),
                        1000,
                        0
                );

                marathon.addCourse(course);
                marathonRepository.save(marathon);
            }

            System.out.println("테스트 유저 / 마라톤 / 코스 생성 완료");
            System.out.println("participant: user@test.com / Password123!");
            System.out.println("organizer: organizer@test.com / Password123!");
        };
    }
}