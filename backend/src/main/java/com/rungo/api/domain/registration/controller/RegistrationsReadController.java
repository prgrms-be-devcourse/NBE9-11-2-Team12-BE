package com.rungo.api.domain.registration.controller;

import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.dto.RegistrationOverviewRes;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;
import com.rungo.api.domain.registration.service.RegistrationReadService;
import com.rungo.api.global.response.ApiResponse;
import com.rungo.api.global.security.SecurityUser;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class RegistrationsReadController {

    private final RegistrationReadService registrationReadService;

    // 인증된 사용자의 접수 목록 조회
    @GetMapping("/api/v1/registrations/me")
    public ResponseEntity<ApiResponse<MyRegistrationRes>> getMyRegistrations(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(required = false) RegistrationStatus status,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하여야 합니다.") int size
    ) {
        // 신청일 기준 최신순 정렬
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("appliedAt"), Sort.Order.desc("id")));

        MyRegistrationRes result = registrationReadService.getMyRegistrations(user.getId(), status, pageable);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // 주최자 - 접수 현황 조회
    @GetMapping("/api/v1/organizer/marathons/{id}/registrations")
    public ResponseEntity<ApiResponse<RegistrationOverviewRes>> getMarathonParticipants(
            @AuthenticationPrincipal(expression = "id") Long organizerId,
            @PathVariable("id") Long marathonId
    ) {

        RegistrationOverviewRes result = registrationReadService.getMarathonParticipants(organizerId, marathonId);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
