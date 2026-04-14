package com.rungo.api.domain.auth.dto;


import com.rungo.api.domain.users.enumtype.Gender;
import com.rungo.api.domain.users.enumtype.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SignUpRes(

        Long id,
        String email,
        String name,
        String phoneNumber,
        Gender gender,
        LocalDate birth,
        Role role,
        LocalDateTime createdAt

) {}