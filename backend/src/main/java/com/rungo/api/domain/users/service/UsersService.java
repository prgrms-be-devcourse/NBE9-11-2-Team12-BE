package com.rungo.api.domain.users.service;

import com.rungo.api.domain.users.dto.MyProfileRes;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.repository.UsersRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public MyProfileRes getMyInfo(Long userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new MyProfileRes(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getBirth(),
                user.getRole()
        );
    }
}