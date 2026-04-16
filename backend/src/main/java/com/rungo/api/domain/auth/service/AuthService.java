package com.rungo.api.domain.auth.service;

import com.rungo.api.domain.auth.dto.*;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.enumtype.Role;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.exception.CustomException;
import com.rungo.api.global.exception.ErrorCode;
import com.rungo.api.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Transactional
    public SignUpRes signup(SignUpReq req) {

        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        Users user = Users.builder()
                          .email(req.email())
                          .password(passwordEncoder.encode(req.password()))
                          .name(req.name())
                          .phoneNumber(req.phoneNumber())
                          .gender(req.gender())
                          .birth(req.birth())
                          .role(Role.PARTICIPANT) // PARTICIPANT 고정
                          .build();

        Users savedUser = userRepository.save(user);

        return new SignUpRes(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getPhoneNumber(),
                savedUser.getGender(),
                savedUser.getBirth(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    @Transactional
    public LoginResult login(LoginReq req) {

        Users user = userRepository.findByEmail(req.email())
                                   .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = JwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                jwtSecret
        );

        String refreshToken = JwtUtil.generateRefreshToken(
                user.getId(),
                user.getEmail(),
                jwtSecret
        );

        refreshTokenService.save(user.getId(), refreshToken); // Redis에 refreshToken 저장
        log.info("Saving refreshToken: {}", user.getId());

        LoginRes loginRes = new LoginRes(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole()
        );

        return new LoginResult(accessToken, refreshToken, loginRes);
    }

    @Transactional
    public String refresh(String refreshToken) {

        // refreshToken null 체크
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 토큰 검증
        if (!JwtUtil.validateToken(refreshToken, jwtSecret)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // userId 추출
        Long userId = JwtUtil.getUserId(refreshToken, jwtSecret);

        // Redis 조회
        String storedRefreshToken = refreshTokenService.getRefreshToken(userId);

        if (storedRefreshToken == null) {
            // 로그아웃 상태 or Redis 만료
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 토큰 불일치
        if (!storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_MISMATCH);
        }

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // accessToken 재발급 (refreshToken은 갱신하지 않음)
        return JwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                jwtSecret
        );
    }
}