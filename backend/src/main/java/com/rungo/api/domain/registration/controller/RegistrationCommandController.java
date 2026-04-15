package com.rungo.api.domain.registration.controller;

import com.rungo.api.domain.registration.dto.CreateRegistrationReq;
import com.rungo.api.domain.registration.dto.CreateRegistrationRes;
import com.rungo.api.domain.registration.service.RegistrationCommandService;
import com.rungo.api.global.response.ApiResponse;
import com.rungo.api.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registrations")
public class RegistrationCommandController {

    private final RegistrationCommandService registrationCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateRegistrationRes>> create(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody CreateRegistrationReq request
    ) {
        CreateRegistrationRes createRegistrationRes = registrationCommandService.create(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("접수가 완료되었습니다.", createRegistrationRes));
    }

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long registrationId
    ) {
        registrationCommandService.cancel(user.getId(), registrationId);

        return ResponseEntity.ok(ApiResponse.okMessage("접수가 취소되었습니다."));
    }
}
