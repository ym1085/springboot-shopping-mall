package com.shoppingmall.exception;

import com.shoppingmall.common.ErrorCode;

public class PasswordNotFoundException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    public PasswordNotFoundException() {
        super(ErrorCode.NOT_FOUND_MEMBER_PASSWORD);
    }
}
