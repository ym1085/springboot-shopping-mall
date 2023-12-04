package com.shoppingmall.api;

import com.shoppingmall.common.ApiUtils;
import com.shoppingmall.common.CommonResponse;
import com.shoppingmall.common.SuccessCode;
import com.shoppingmall.dto.request.CommentRequestDto;
import com.shoppingmall.dto.response.CommentResponseDto;
import com.shoppingmall.exception.InvalidParameterException;
import com.shoppingmall.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1")
@RestController
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}/comments")
    public ResponseEntity<CommonResponse> saveComment(
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            @PathVariable("postId") Long postId,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }

        commentRequestDto.setMemberId(1L);
        commentRequestDto.setPostId(postId);

        List<CommentResponseDto> comments = commentService.saveComment(commentRequestDto);
        return ApiUtils.success(SuccessCode.SUCCESS_SAVE_COMMENT.getCode(), SuccessCode.SUCCESS_SAVE_COMMENT.getMessage(), comments, HttpStatus.OK);
    }

    @PutMapping("/post/{postId}/comments")
    public ResponseEntity<CommonResponse> updateCommentByCommentId(
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            @PathVariable("postId") Long postId,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new InvalidParameterException(bindingResult);
        }

        commentRequestDto.setPostId(postId);

        List<CommentResponseDto> comments = commentService.updateCommentByCommentId(commentRequestDto);
        return ApiUtils.success(SuccessCode.SUCCESS_UPDATE_COMMENT.getCode(), SuccessCode.SUCCESS_UPDATE_COMMENT.getMessage(), comments, HttpStatus.OK);
    }

    @DeleteMapping("/post/comments")
    public ResponseEntity<CommonResponse> deleteComments(
            @ModelAttribute CommentRequestDto commentRequestDto) {

        List<CommentResponseDto> comments = commentService.deleteComments(commentRequestDto);

        return ApiUtils.success(SuccessCode.SUCCESS_DELETE_COMMENT.getCode(), SuccessCode.SUCCESS_DELETE_COMMENT.getMessage(), comments, HttpStatus.OK);
    }

    @DeleteMapping("/post/comments/reply")
    public ResponseEntity<CommonResponse> deleteCommentsReply(
            @ModelAttribute CommentRequestDto commentRequestDto) {

        List<CommentResponseDto> comments = commentService.deleteCommentsReply(commentRequestDto);

        return ApiUtils.success(SuccessCode.SUCCESS_DELETE_COMMENT.getCode(), SuccessCode.SUCCESS_DELETE_COMMENT.getMessage(), comments, HttpStatus.OK);
    }
}
