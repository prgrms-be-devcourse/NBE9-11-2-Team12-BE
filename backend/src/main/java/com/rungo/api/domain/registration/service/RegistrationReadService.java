package com.rungo.api.domain.registration.service;

import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationReadService {

    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MyRegistrationRes> getMyRegistrations(String email) {

        // 이메일로 사용자 조회
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자의 id로 DB 조회 후 신청일 기준 내림차순 정렬
        return registrationRepository.findAllByUser_IdOrderByAppliedAtDesc(user.getId())
                .stream()
                .map(MyRegistrationRes::from)
                .toList();
    }

}
