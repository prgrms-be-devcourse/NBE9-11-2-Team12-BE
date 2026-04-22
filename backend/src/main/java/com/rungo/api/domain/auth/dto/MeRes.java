package com.rungo.api.domain.auth.dto;

import com.rungo.api.domain.users.entity.Users;
import lombok.Builder;

@Builder
public record MeRes(
        Long id,
        String email,
        String name,
        String role
) {
    public static MeRes from(Users user) {
        return MeRes.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole().name())
                    .build();
    }
}