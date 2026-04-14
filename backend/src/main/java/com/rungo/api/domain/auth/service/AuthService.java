package com.rungo.api.domain.auth.service;

import com.rungo.api.domain.auth.dto.LoginDto;
import com.rungo.api.domain.auth.dto.SignUpDto;
import com.rungo.api.domain.users.entity.Users;
import com.rungo.api.domain.users.repository.UserRepository;
import com.rungo.api.global.util.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public void signup(SignUpDto req) {

        Users user = Users.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .gender(req.getGender())
                .birth(req.getBirth())
                .role(req.getRole())
                .build();

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        userRepository.save(user);
    }

    public Map<String, String> login(LoginDto req) {

        Users user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일입니다."));



        // 비밀번호 검증
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 생성
        String accessToken = Jwt.jwt.generateToken(user.getEmail(), user.getRole(), jwtSecret);

        String refreshToken = Jwt.jwt.toString(
                jwtSecret,
                60 * 60 * 24 * 7, // 7일
                Map.of("sub", user.getEmail()) // email로 사용자 인증
        );

        // TODO: refreshToken 저장

        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }
}