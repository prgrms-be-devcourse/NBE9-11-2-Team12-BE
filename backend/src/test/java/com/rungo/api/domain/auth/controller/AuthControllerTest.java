package com.rungo.api.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rungo.api.domain.auth.dto.AuthDto;
import com.rungo.api.domain.auth.service.AuthService;
import com.rungo.api.domain.users.enumtype.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공 - 201 상태코드와 공통 응답 규격이 반환된다")
    void signup_success() throws Exception {
        // given
        AuthDto.SignUpReq req = new AuthDto.SignUpReq(
                "test@test.com", "Password123!", "홍길동",
                "010-1234-5678", Gender.MALE, LocalDate.of(1995, 1, 1)
        );

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.status").value(201))
               .andExpect(jsonPath("$.code").value("SUCCESS"))
               .andExpect(jsonPath("$.message").value("회원가입 성공"));
    }

    @Test
    @DisplayName("로그인 성공 - 200 상태코드와 토큰 데이터가 반환된다")
    void login_success() throws Exception {
        AuthDto.LoginReq req = new AuthDto.LoginReq("test@test.com", "Password123!");
        AuthDto.LoginRes res = new AuthDto.LoginRes("access-token", "refresh-token");

        given(authService.login(any())).willReturn(res);

        mockMvc.perform(post("/api/v1/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value(200))
               .andExpect(jsonPath("$.data.accessToken").value("access-token"))
               .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("유효성 검증 실패 - 이메일 형식이 틀리면 400 에러를 반환한다")
    void signup_fail_invalid_email() throws Exception {
        AuthDto.SignUpReq req = new AuthDto.SignUpReq(
                "invalid-email", "Password123!", "홍길동",
                "010-1234-5678", Gender.MALE, LocalDate.of(1995, 1, 1)
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }
}