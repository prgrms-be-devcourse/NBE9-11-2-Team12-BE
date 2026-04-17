package com.rungo.api.domain.marathon.marathon.controller;

import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonReq;
import com.rungo.api.domain.marathon.marathon.dto.create.CreateMarathonRes;
import com.rungo.api.domain.marathon.marathon.dto.delete.CancelMarathonRes;
import com.rungo.api.domain.marathon.marathon.dto.read.MarathonDetailRes;
import com.rungo.api.domain.marathon.marathon.dto.read.MarathonListRes;
import com.rungo.api.domain.marathon.marathon.dto.update.UpdateMarathonReq;
import com.rungo.api.domain.marathon.marathon.dto.update.UpdateMarathonRes;
import com.rungo.api.domain.marathon.marathon.repository.MarathonRepository;
import com.rungo.api.domain.marathon.marathon.service.MarathonService;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import com.rungo.api.global.response.ApiResponse;
import com.rungo.api.global.security.SecurityUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/marathons")
@RequiredArgsConstructor
public class MarathonController {

    private final MarathonService marathonService;
    private final MarathonRepository marathonRepository;

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

    @GetMapping
    public ResponseEntity<ApiResponse<MarathonListRes>> getMarathonList(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(marathonService.getMarathons(pageable)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MarathonDetailRes>> getMarathonDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(marathonService.getMarathonDetail(id)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<CancelMarathonRes>> cancelMarathon(
            @AuthenticationPrincipal SecurityUser user,
            @PathVariable Long id
    ) {
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        CancelMarathonRes res = marathonService.cancelMarathon(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

}
