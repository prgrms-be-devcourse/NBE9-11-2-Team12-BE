package com.rungo.api.domain.marathon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rungo.api.domain.marathon.marathon.controller.MarathonController;
import com.rungo.api.domain.marathon.marathon.dto.CourseItemRes;
import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonReq;
import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonRes;
import com.rungo.api.domain.marathon.marathon.enumtype.MarathonStatus;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.marathon.marathon.service.MarathonService;
import com.rungo.api.global.security.SecurityUser;
import com.rungo.api.domain.users.enumtype.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(MarathonController.class)
class MarathonControllerTest {

    @Autowired

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean

    private MarathonService marathonService;

    @MockitoBean
    private MarathonRepository marathonRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthenticatedUser(Long userId) {
        SecurityUser user = new SecurityUser(
                userId,
                "test@test.com",
                Role.ORGANIZER,
                List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER"))
        );

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("마라톤 생성 성공 - 201 상태코드와 공통 응답을 반환한다")
    void create_success() throws Exception {

        setAuthenticatedUser(1L);

        CreateMarathonReq req = new CreateMarathonReq(
                "서울 마라톤",
                "서울",
                LocalDate.of(2026, 10, 3),
                "poster.png",
                LocalDateTime.of(2026, 8, 1, 9, 0),
                LocalDateTime.of(2026, 8, 10, 18, 0),
                List.of(
                        new CreateMarathonReq.CreateCourseItemReq(
                                "10K",
                                BigDecimal.valueOf(30000),
                                100
                        )
                )
        );

        CreateMarathonRes res = new CreateMarathonRes(
                1L,
                "서울 마라톤",
                "서울",
                LocalDate.of(2026, 10, 3),
                "poster.png",
                LocalDateTime.of(2026, 8, 1, 9, 0),
                LocalDateTime.of(2026, 8, 10, 18, 0),
                MarathonStatus.OPEN,
                List.of(
                        new CourseItemRes(
                                11L,
                                "10K",
                                BigDecimal.valueOf(30000),
                                100,
                                0,
                                100
                        )
                ),
                LocalDateTime.of(2026, 7, 1, 12, 0)
        );

        given(marathonService.createMarathon(eq(1L), eq(req))).willReturn(res);

        mockMvc.perform(post("/api/v1/marathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(marathonService).createMarathon(eq(1L), eq(req));
    }

    @Test
    @DisplayName("마라톤 생성 실패 - title이 없으면 400 반환")
    void create_fail_title_null() throws Exception {

        setAuthenticatedUser(1L);

        String request = """
            {
              "region": "서울",
              "eventDate": "2026-10-03",
              "registrationStartAt": "2026-08-01T09:00:00",
              "registrationEndAt": "2026-08-10T18:00:00",
              "courses": [
                {
                  "courseType": "10K",
                  "price": 30000,
                  "capacity": 100
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/marathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE"));

        verifyNoInteractions(marathonService);
    }

    @Test
    @DisplayName("마라톤 생성 실패 - courses가 비어있으면 400 반환")
    void create_fail_courses_empty() throws Exception {

        setAuthenticatedUser(1L);

        String request = """
            {
              "title": "서울 마라톤",
              "region": "서울",
              "eventDate": "2026-10-03",
              "registrationStartAt": "2026-08-01T09:00:00",
              "registrationEndAt": "2026-08-10T18:00:00",
              "courses": []
            }
            """;

        mockMvc.perform(post("/api/v1/marathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(marathonService);
    }

    @Test
    @DisplayName("마라톤 생성 실패 - courseType이 비어있으면 400 반환")
    void create_fail_courseType_Null() throws Exception {

        setAuthenticatedUser(1L);

        String request = """
            {
              "title": "서울 마라톤",
              "region": "서울",
              "eventDate": "2026-10-03",
              "registrationStartAt": "2026-08-01T09:00:00",
              "registrationEndAt": "2026-08-10T18:00:00",
              "courses": [
                {
                  
                  "price": 30000,
                  "capacity": 100
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/marathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(marathonService);
    }

    @Test
    @DisplayName("마라톤 생성 실패 - courseType의 문자열에 빈 공백들어 올 시 400 반환")
    void create_fail_courseType_blank() throws Exception {

        setAuthenticatedUser(1L);

        String request = """
            {
              "title": "서울 마라톤",
              "region": "서울",
              "eventDate": "2026-10-03",
              "registrationStartAt": "2026-08-01T09:00:00",
              "registrationEndAt": "2026-08-10T18:00:00",
              "courses": [
                {
                  "courseType": "   ",
                  "price": 30000,
                  "capacity": 100
                }
              ]
            }
            """;

        mockMvc.perform(post("/api/v1/marathons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(marathonService);
    }
}