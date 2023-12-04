package com.shoppingmall.exception;

import com.shoppingmall.common.ErrorCode;

public class FailUpdatePostException extends CustomException {

    private static final long serialVersionUID = -2116671122895194101L;

    public FailUpdatePostException() {
        super(ErrorCode.FAIL_UPDATE_POST);
    }
}