package com.rungo.api.domain.marathon.marathon.controller;

import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonReq;
import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonRes;
import com.rungo.api.domain.marathon.marathon.service.MarathonService;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import com.rungo.api.global.response.ApiResponse;
import com.rungo.api.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/marathons")
@RequiredArgsConstructor
public class MarathonController {

    private final MarathonService marathonService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateMarathonRes>> createMarathon(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody CreateMarathonReq req
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        CreateMarathonRes res = marathonService.createMarathon(user.getId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("마라톤 대회 생성 성공", res));
    }
}
