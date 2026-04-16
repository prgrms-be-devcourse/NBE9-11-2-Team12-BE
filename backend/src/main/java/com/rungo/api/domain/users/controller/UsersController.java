package com.rungo.api.domain.users.controller;

import com.rungo.api.domain.users.dto.MyProfileRes;
import com.rungo.api.domain.users.dto.UpdateMyProfileReq;
import com.rungo.api.domain.users.dto.UpdateMyProfileRes;
import com.rungo.api.domain.users.service.UsersService;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import com.rungo.api.global.response.ApiResponse;
import com.rungo.api.global.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "사용자 컨트롤러, 사용자와 관련된 API를 제공합니다.")
public class UsersController {

    private final UsersService userService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "내 정보 조회 API입니다. 인증된 사용자만 접근할 수 있습니다.")
    public MyProfileRes getMyInfo(@AuthenticationPrincipal SecurityUser user) {
        // 인증이 실패된 객체라면
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        return userService.getMyInfo(user.getId());
    }

    @PatchMapping("/me")
    @Operation(summary = "내 정보 수정", description = "내 정보 수정 API입니다. 인증된 사용자만 접근할 수 있습니다.")
    public ResponseEntity<ApiResponse<UpdateMyProfileRes>> updateMyProfile(
            @AuthenticationPrincipal SecurityUser user,
            @Valid @RequestBody UpdateMyProfileReq req
    ) {

        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        UpdateMyProfileRes res = userService.updateMyProfile(user.getId(), req);

        return ResponseEntity.ok(ApiResponse.ok(res));
    }
}