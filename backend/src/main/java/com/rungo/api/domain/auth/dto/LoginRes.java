package com.rungo.api.domain.auth.dto;

import com.rungo.api.domain.users.enumtype.Role;

public record LoginRes(

        Long id,
        String email,
        String name,
        Role role

) {}