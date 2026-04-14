package com.rungo.api.domain.auth.service;

import com.rungo.api.domain.auth.dto.LoginReq;
import com.rungo.api.domain.auth.dto.SignUpReq;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Gender;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        // @Value("${jwt.secret}") 값을 테스트 환경에서 강제로 주입
        ReflectionTestUtils.setField(authService, "jwtSecret", "test-secret-key-at-least-32-bytes-long");
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일이 중복되면 DUPLICATE_EMAIL 예외가 발생한다")
    void signup_fail_duplicate_email() {

        SignUpReq req = new SignUpReq(
                "duplicate@test.com", "pass123!", "name", "010-1111-2222",
                Gender.MALE, LocalDate.of(2000, 1, 1)
        );
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(Users.builder().build()));

        CustomException exception = assertThrows(CustomException.class, () -> authService.signup(req));
        assertEquals(ErrorCode.DUPLICATE_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않으면 INVALID_CREDENTIALS 예외가 발생한다")
    void login_fail_password_mismatch() {
        LoginReq req = new LoginReq("test@test.com", "wrong-pass");
        Users user = Users.builder().email("test@test.com").password("encoded-pass").build();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        // 비밀번호 불일치
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        CustomException exception = assertThrows(CustomException.class, () -> authService.login(req));
        assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getErrorCode());
    }
}