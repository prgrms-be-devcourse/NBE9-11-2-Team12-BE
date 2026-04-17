package com.rungo.api.domain.users.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateMyProfileReq (

        @Size(min = 1, message = "이름은 1자 이상이어야 합니다.")
        String name,

        @Pattern(
                regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)"
        )
        String phoneNumber

) {}
