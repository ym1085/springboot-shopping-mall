package com.shoppingmall.exception;

import com.shoppingmall.common.response.ErrorCode;

public class FailSaveMemberException extends CustomException {

    public FailSaveMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
