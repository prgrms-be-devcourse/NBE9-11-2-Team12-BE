package com.rungo.api.domain.auth.controller;

import com.rungo.api.domain.auth.dto.LoginDto;
import com.rungo.api.domain.auth.dto.SignUpDto;
import com.rungo.api.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name="Auth", description = "인증 컨트롤러, 인증과 관련된 API를 제공합니다.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입 API입니다. 회원가입이 성공하면 '회원가입 성공' 메시지를 반환합니다.")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpDto req) {

        authService.signup(req);

        return ResponseEntity.ok(Map.of(
                "message", "회원가입 성공"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto req) {

        Map<String, String> tokens = authService.login(req);

        return ResponseEntity.ok(Map.of(
                "message", "로그인 성공",
                "accessToken", tokens.get("accessToken"),
                "refreshToken", tokens.get("refreshToken")
        ));
    }
}