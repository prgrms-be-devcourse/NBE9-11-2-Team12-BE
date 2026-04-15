package com.rungo.api.domain.registration.controller;

import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.service.RegistrationReadService;
import com.rungo.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RegistrationsReadController {

    private final RegistrationReadService registrationReadService;

    // 내 접수 조회
    // 인증된 사용자의 id를 가져와 접수 목록 조회
    @GetMapping("/api/v1/registrations/me")
    public ResponseEntity<ApiResponse<List<MyRegistrationRes>>> getMyRegistrations(@AuthenticationPrincipal(expression = "id") Long userId) {

        List<MyRegistrationRes> result = registrationReadService.getMyRegistrations(userId);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
