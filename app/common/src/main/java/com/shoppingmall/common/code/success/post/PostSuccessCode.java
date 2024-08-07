package com.shoppingmall.common.code.success.post;

import com.shoppingmall.common.code.success.SuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostSuccessCode implements SuccessCode {
    SUCCESS_SAVE_POST(HttpStatus.OK, "게시글 등록에 성공하였습니다."),
    SUCCESS_DELETE_POST(HttpStatus.OK, "게시글 삭제에 성공하였습니다."),
    SUCCESS_UPDATE_POST(HttpStatus.OK, "게시글 수정에 성공하였습니다."),
    SUCCESS_SAVE_COMMENT(HttpStatus.OK, "게시글 댓글 저장에 성공하였습니다."),
    SUCCESS_DELETE_COMMENT(HttpStatus.OK, "게시글 댓글 삭제에 성공하였습니다."),
    SUCCESS_UPDATE_COMMENT(HttpStatus.OK, "게시글 댓글 수정에 성공하였습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}