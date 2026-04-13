package com.rungo.api.global.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiResponse<T> {

    private int status;
    private String code;
    private String message;
    private T data;

    // 1. 조회 성공 (200 OK + 데이터 반환)
    // 사용 예시: return ResponseEntity.ok(ApiResponse.ok(userDto));
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", "요청에 성공했습니다.", data);
    }

    // 2. 생성 성공 (201 Created + 커스텀 메시지 + 데이터 반환)
    // 사용 예시: return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("회원가입 성공", data));
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(HttpStatus.CREATED.value(), "SUCCESS", message, data);
    }

    // 3. 데이터 없는 성공 (200 OK + 커스텀 메시지 반환)
    // 사용 예시: return ResponseEntity.ok(ApiResponse.okMessage("접수가 취소되었습니다."));
    public static ApiResponse<Void> okMessage(String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", message, null);
    }

    // 4. 에러 응답 (상세 검증 에러 데이터 포함용)
    public static <T> ApiResponse<T> error(HttpStatus status, String code, String message, T data) {
        return new ApiResponse<>(status.value(), code, message, data);
    }

    // 5. 에러 응답 (기본)
    public static ApiResponse<Void> error(HttpStatus status, String code, String message) {
        return new ApiResponse<>(status.value(), code, message, null);
    }
}