package com.rungo.api.global.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

    private int status;
    private String code;
    private String message;
    private T data;

    // 성공 - 데이터/메세지 0
    public static <T> ApiResponse<T> success(HttpStatus status, String message, T data){
        return new ApiResponse<>(status.value(), "SUCCESS", message, data);
    }

    // 성공 - 데이터 x
    public static <T> ApiResponse<T> success(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), "SUCCESS", message, null);
    }

    // 성공
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "SUCCESS", "요청에 성공했습니다.", data);
    }

    // 에러 - 데이터 x, 코드/메세지 o
    public static <T> ApiResponse<T> error(HttpStatus status, String code, String message) {
        return new ApiResponse<>(status.value(), code, message, null);
    }

    // 에러 - 상세 데이터 ex) 검증 실패 시 데이터
    public static <T> ApiResponse<T> error(HttpStatus status, String code, String message, T data) {
        return new ApiResponse<>(status.value(), code, message, data);
    }
}
