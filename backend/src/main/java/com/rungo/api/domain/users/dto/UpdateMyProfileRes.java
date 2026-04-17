package com.rungo.api.domain.users.dto;

import com.rungo.api.domain.users.enumtype.Gender;
import com.rungo.api.domain.users.enumtype.Role;

import java.time.LocalDate;

public record UpdateMyProfileRes (

        Long id,
        String email,
        String name,
        String phoneNumber,
        Gender gender,
        LocalDate birth,
        Role role

) {}
