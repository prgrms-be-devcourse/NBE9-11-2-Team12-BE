package com.rungo.api.domain.auth.controller;

import com.rungo.api.domain.auth.dto.*;
import com.rungo.api.domain.auth.service.AuthService;
import com.rungo.api.global.response.ApiResponse;
import com.rungo.api.global.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 컨트롤러, 인증과 관련된 API를 제공합니다.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입 API입니다.")
    public ResponseEntity<ApiResponse<SignUpRes>> signup(@Valid @RequestBody SignUpReq req) {

        SignUpRes res = authService.signup(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("회원가입 성공", res));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 API입니다.")
    public ResponseEntity<ApiResponse<LoginRes>> login(
            @Valid @RequestBody LoginReq req,
            HttpServletResponse response
    ) {
        LoginResult result = authService.login(req);

        int accessExpire = 60 * 60; // 1시간
        int refreshExpire = 60 * 60 * 24 * 7; // 7일

        CookieUtil.addCookie(response, "accessToken", result.accessToken(), accessExpire);
        CookieUtil.addCookie(response, "refreshToken", result.refreshToken(), refreshExpire);

        return ResponseEntity.ok(ApiResponse.ok(result.loginRes()));
    }
}