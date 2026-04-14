package com.rungo.api.domain.registration.controller;

import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.service.RegistrationReadService;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import com.rungo.api.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RegistrationsReadController {
    private final RegistrationReadService registrationReadService;

    // 내 접수 조회
    @GetMapping("/api/v1/registration/me")
    public ApiResponse<List<MyRegistrationRes>> getMyRegistrations(Authentication authentication) {

        // 인증 정보가 없거나 유효하지 않은 경우 접근 차단
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 인증된 사용자의 이메일 기반으로 신청 내역 조회
        return ApiResponse.ok(registrationReadService.getMyRegistrations(authentication.getName()));
    }
}
