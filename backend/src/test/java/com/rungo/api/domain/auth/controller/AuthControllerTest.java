package com.rungo.api.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rungo.api.domain.auth.dto.LoginReq;
import com.rungo.api.domain.auth.dto.LoginRes;
import com.rungo.api.domain.auth.dto.LoginResult;
import com.rungo.api.domain.auth.dto.SignUpReq;
import com.rungo.api.domain.auth.service.AuthService;
import com.rungo.api.domain.users.enumtype.Gender;
import com.rungo.api.domain.users.enumtype.Role;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

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
        SignUpReq req = new SignUpReq(
                "test@test.com", "Password123!", "홍길동",
                "010-1234-5678", Gender.MALE, LocalDate.of(1999, 1, 1)
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.status").value(201))
               .andExpect(jsonPath("$.code").value("SUCCESS"))
               .andExpect(jsonPath("$.message").value("회원가입 성공"));
    }

    @Test
    @DisplayName("로그인 성공 - 200 상태코드와 쿠키에 토큰이 담겨 반환된다")
    void login_success() throws Exception {
        LoginReq req = new LoginReq("test@test.com", "Password123!");
        LoginRes loginRes = new LoginRes(1L, "test@test.com", "홍길동", Role.PARTICIPANT);
        LoginResult result = new LoginResult("access-token", "refresh-token", loginRes);

        given(authService.login(any())).willReturn(result);

        mockMvc.perform(post("/api/v1/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.status").value(200))
               .andExpect(jsonPath("$.data.email").value("test@test.com")) // JSON 응답 검증
               .andExpect(cookie().exists("accessToken")) // 쿠키 검증
               .andExpect(cookie().exists("refreshToken"));
    }

    @Test
    @DisplayName("유효성 검증 실패 - 이메일 형식이 틀리면 400 에러를 반환한다")
    void signup_fail_invalid_email() throws Exception {
        SignUpReq req = new SignUpReq(
                "invalid-email", "Password123!", "홍길동",
                "010-1234-5678", Gender.MALE, LocalDate.of(1999, 1, 1)
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }
}