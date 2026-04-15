package com.rungo.api.domain.registration.service;

import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationReadService {

    private final RegistrationRepository registrationRepository;

    @Transactional(readOnly = true)
    public List<MyRegistrationRes> getMyRegistrations(Long userId) {

        // 사용자의 id로 DB 조회 후 신청일 기준 내림차순 정렬
        return registrationRepository.findAllByUser_IdOrderByAppliedAtDesc(userId)
                .stream()
                .map(MyRegistrationRes::from)
                .toList();
    }

}
