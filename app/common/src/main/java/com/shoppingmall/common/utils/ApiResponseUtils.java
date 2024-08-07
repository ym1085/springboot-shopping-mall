package com.shoppingmall.common.utils;

import com.shoppingmall.common.code.failure.FailureCode;
import com.shoppingmall.common.code.success.SuccessCode;
import com.shoppingmall.common.dto.BaseResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseUtils {

    public static <T> ResponseEntity<BaseResponse<?>> success(SuccessCode code, T data) {
        return ResponseEntity
                .status(code.getStatus())
                .body(BaseResponse.of(code.getMessage(), data));
    }

    public static ResponseEntity<BaseResponse<?>> success(SuccessCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(BaseResponse.of(true, code.getMessage()));
    }

    public static <T> ResponseEntity<BaseResponse<?>> success(SuccessCode code, HttpHeaders headers, T data) {
        return ResponseEntity
                .status(code.getStatus())
                .headers(headers)
                .body(BaseResponse.of(code.getMessage(), data));
    }

    public static <T> ResponseEntity<BaseResponse<?>> failure(FailureCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(BaseResponse.of(false, code.getMessage()));
    }

    public static <T> ResponseEntity<BaseResponse<?>> failure(String code) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.of(false, code));
    }
}