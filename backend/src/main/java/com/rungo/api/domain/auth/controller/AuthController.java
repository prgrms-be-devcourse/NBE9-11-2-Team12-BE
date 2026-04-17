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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 컨트롤러, 인증과 관련된 API를 제공합니다.")
public class AuthController {

    private final AuthService authService;

    private static final int ACCESS_TOKEN_EXPIRE = 60 * 60; // 1시간
    private static final int REFRESH_TOKEN_EXPIRE = 60 * 60 * 24 * 7; // 7일

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

        CookieUtil.addCookie(response, "accessToken", result.accessToken(), ACCESS_TOKEN_EXPIRE);
        CookieUtil.addCookie(response, "refreshToken", result.refreshToken(), REFRESH_TOKEN_EXPIRE);

        return ResponseEntity.ok(ApiResponse.ok(result.loginRes()));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 API입니다. 쿠키를 삭제하고 Redis의 refreshToken을 제거합니다.")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.okMessage("로그아웃되었습니다."));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급 API입니다. refreshToken을 가지고 accessToken 및 refreshToken을 재발급합니다.")
    public ResponseEntity<ApiResponse<Void>> reissue(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        TokenRes tokenRes = authService.tokenReissue(refreshToken);

        CookieUtil.addCookie(response, "accessToken", tokenRes.accessToken(), ACCESS_TOKEN_EXPIRE);
        CookieUtil.addCookie(response, "refreshToken", tokenRes.refreshToken(), REFRESH_TOKEN_EXPIRE);

        return ResponseEntity.ok(ApiResponse.okMessage("토큰이 재발급되었습니다."));
    }
}