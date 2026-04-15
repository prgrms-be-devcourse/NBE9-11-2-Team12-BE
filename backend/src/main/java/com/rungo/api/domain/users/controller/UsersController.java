package com.rungo.api.domain.users.controller;

import com.rungo.api.domain.users.dto.MyProfileRes;
import com.rungo.api.domain.users.service.UsersService;
import com.rungo.api.global.security.SecurityUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "사용자 컨트롤러, 사용자와 관련된 API를 제공합니다.")
public class UsersController {

    private final UsersService userService;

    @GetMapping("/me")
    public MyProfileRes getMyInfo(
            @AuthenticationPrincipal SecurityUser user
    ) {
        return userService.getMyInfo(user.getId());
    }
}