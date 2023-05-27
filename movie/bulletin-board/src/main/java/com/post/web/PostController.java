package com.post.web;

import com.post.constant.ResponseCode;
import com.post.constant.StatusEnum;
import com.post.service.FileService;
import com.post.service.PostService;
import com.post.utils.FileUtils;
import com.post.web.dto.request.FileRequestDto;
import com.post.web.dto.request.PostRequestDto;
import com.post.web.dto.request.SearchRequestDto;
import com.post.web.dto.resposne.Message;
import com.post.web.dto.resposne.PagingResponseDto;
import com.post.web.dto.resposne.PostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * Fixme: ResponseEntity 목적에 맞게 반환 하도록 수정 필요
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final FileUtils fileUtils;
    private final FileService fileService;

    @GetMapping(value = "/post")
    public ResponseEntity getPosts(SearchRequestDto searchRequestDto) {
        PagingResponseDto<PostResponseDto> posts = postService.getPosts(searchRequestDto);
        Message message = new Message<>(StatusEnum.OK, ResponseCode.SUCCESS.getResponseCode(), posts);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping(value = "/post/{id}")
    public ResponseEntity getPostById(@PathVariable("id") Long id) {
        PostResponseDto post = postService.getPostById(id);
        Message message = new Message<>(StringUtils.isNotBlank(post.getTitle())
                ? StatusEnum.OK : StatusEnum.INTERNAL_SERVER_ERROR, ResponseCode.SUCCESS.getResponseCode(), post);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping(value = "/post")
    public ResponseEntity savePost(@Valid PostRequestDto postRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Message message = new Message(StatusEnum.BAD_REQUEST, Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        postRequestDto.setMemberId(1L); // Todo: Test용 member_id security 되면 지워주세요
        Long postId = postService.savePost(postRequestDto);

        List<FileRequestDto> fileRequestDtos = fileUtils.uploadFiles(postRequestDto.getFiles());
        fileService.saveFiles(postId, fileRequestDtos);

        return new ResponseEntity<>(new Message<>(StatusEnum.OK, ResponseCode.SUCCESS.getResponseCode()), HttpStatus.OK);
    }

    @PutMapping(value = "/post/{id}")
    public ResponseEntity uploadPost(@PathVariable("id") Long id,
                                     @Valid PostRequestDto postRequestDto,
                                     BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Message message = new Message(StatusEnum.BAD_REQUEST, Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        // Todo: 현재 로그인 한 Principal 유저 정보와 질의를 통해 얻은 User 정보를 비교 후 수정 여부 판단,
        // 회원 쪽 기능이 구현이 되지 않아 일단 보류

        postRequestDto.setMemberId(1L); // Todo: Test용 member_id security 되면 지워주세요
        postRequestDto.setPostId(id);

        return new ResponseEntity<>(new Message<>(StatusEnum.OK, ResponseCode.SUCCESS.getResponseCode(), postService.uploadPost(postRequestDto)), HttpStatus.OK);
    }

    @DeleteMapping(value = "/post/{id}")
    public ResponseEntity deletePost(@PathVariable("id") Long id) {
        return new ResponseEntity<>(new Message<>(StatusEnum.OK, ResponseCode.SUCCESS.getResponseCode(), postService.deletePost(id)), HttpStatus.OK);
    }
}
