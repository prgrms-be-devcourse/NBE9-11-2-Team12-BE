package com.rungo.api.domain.users.admin.controller;

import com.rungo.api.domain.users.admin.service.AdminService;
import com.rungo.api.domain.users.dto.MyProfileRes;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.global.response.ApiResponse;
import com.rungo.api.global.security.SecurityUser;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @PatchMapping("/{userId}/organizer")

    public ResponseEntity<ApiResponse<MyProfileRes>> approveOrganizer(

            @AuthenticationPrincipal SecurityUser admin,

            @PathVariable @NotNull Long userId

    ) {

        MyProfileRes myProfileRes =adminService.approveOrganizer(admin.getId(), userId);

        return ResponseEntity.ok(ApiResponse.ok(myProfileRes));

    }
}
