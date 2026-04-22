package com.rungo.api.domain.registration.controller;

import com.rungo.api.domain.registration.dto.CreateRegistrationReq;
import com.rungo.api.domain.registration.dto.CreateRegistrationRes;
import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.enumtype.MyRegistrationStatusFilter;
import com.rungo.api.domain.registration.service.RegistrationService;
import com.rungo.api.global.response.ApiResponse;
import com.rungo.api.global.security.SecurityUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateRegistrationRes>> create(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody CreateRegistrationReq request
    ) {
        CreateRegistrationRes createRegistrationRes = registrationService.create(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("접수가 완료되었습니다.", createRegistrationRes));
    }

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long registrationId
    ) {
        registrationService.cancel(user.getId(), registrationId);

        return ResponseEntity.ok(ApiResponse.okMessage("접수가 취소되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyRegistrationRes>> getMyRegistrations(
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam(defaultValue = "ACTIVE") MyRegistrationStatusFilter status,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하여야 합니다.") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        MyRegistrationRes result = registrationService.getMyRegistrations(user.getId(), status, pageable);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
