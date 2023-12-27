package com.shoppingmall.exception;

import com.shoppingmall.common.ErrorCode;

public class PasswordNotMatchedException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    public PasswordNotMatchedException() {
        super(ErrorCode.NOT_FOUND_MEMBER_PASSWORD);
    }
}