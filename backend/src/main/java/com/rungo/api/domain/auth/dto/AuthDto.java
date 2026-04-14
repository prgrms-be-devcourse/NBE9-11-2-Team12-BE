package com.rungo.api.domain.auth.dto;

import com.rungo.api.domain.users.enumtype.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class AuthDto {
    // 회원가입 요청
    public record SignUpReq(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 20) @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*]).+$") String password,
            @NotBlank String name,
            @NotBlank @Pattern(regexp = "^010-\\d{4}-\\d{4}$") String phoneNumber,
            @NotNull Gender gender,
            @NotNull @Past LocalDate birth
    ) {}

    // 로그인 요청
    public record LoginReq(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    // 로그인 응답 (성공 시 토큰 반환용)
    public record LoginRes(
            String accessToken,
            String refreshToken
    ) {}
}