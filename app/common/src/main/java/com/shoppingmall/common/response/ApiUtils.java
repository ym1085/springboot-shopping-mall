package com.shoppingmall.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// [Greeting API] https://help.greetinghr.com/reference/common
public class ApiUtils {

    /**
     * [OK] 데이터 없이 응답값 반환
     * @param status    :   HTTP status object
     * @param message   :   HTTP custom message
     */
    public static ResponseEntity<CommonResponse> success(String code, HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(CommonResponse.builder()
                        .code(code)
                        .message(message)
                        .build());
    }

    /**
     * [OK] 데이터와 함께 응답값 반환
     * @param status    :   HTTP status object
     * @param message   :   HTTP custom message
     * @param result    :   응답 데이터
     */
    public static <T> ResponseEntity<CommonResponse> success(String code, HttpStatus status, String message, T result) {
        return ResponseEntity
                .status(status)
                .body(CommonResponse.builder()
                        .code(code)
                        .message(message)
                        .result(result)
                        .build());
    }

    /**
     * [FAIL] 데이터 없이 실패 응답값 반환
     * @param status    :   HTTP status object
     * @param message   :   HTTP custom message
     */
    public static ResponseEntity<CommonResponse> fail(String code, HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(CommonResponse.builder()
                        .code(code)
                        .message(message)
                        .build());
    }

    /**
     * [FAIL] 데이터와 함께 실패 응답값 반환
     * @param status    :   HTTP status object
     * @param message   :   HTTP custom message
     * @param result    :   응답 데이터
     */
    public static <T> ResponseEntity<CommonResponse> fail(String code, HttpStatus status, String message, T result) {
        return ResponseEntity
                .status(status)
                .body(CommonResponse.builder()
                        .code(code)
                        .message(message)
                        .result(result)
                        .build());
    }
}