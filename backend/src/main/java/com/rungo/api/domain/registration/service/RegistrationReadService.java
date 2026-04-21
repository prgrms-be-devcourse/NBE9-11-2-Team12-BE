package com.rungo.api.domain.registration.service;

import com.rungo.api.domain.registration.dto.MyRegistrationRes;
import com.rungo.api.domain.registration.entity.Registration;
import com.rungo.api.domain.registration.enumtype.RegistrationStatus;
import com.rungo.api.domain.registration.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationReadService {

    private final RegistrationRepository registrationRepository;

    // 내 접수 목록 조회
    @Transactional(readOnly = true)
    public MyRegistrationRes getMyRegistrations(Long userId, RegistrationStatus status, Pageable pageable) {

        Page<Registration> page;

        // 상태 필터 없을 시 전체 목록 조회, 있을 시 해당 상태의 목록만 조회
        if (status == null) {
            page = registrationRepository.findByUser_Id(userId, pageable);
        } else {
            page = registrationRepository.findByUser_IdAndStatus(userId, status, pageable);
        }

        return MyRegistrationRes.from(page);
    }


}
