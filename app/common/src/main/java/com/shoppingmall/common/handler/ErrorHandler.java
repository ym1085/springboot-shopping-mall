package com.shoppingmall.common.handler;

import com.shoppingmall.common.dto.BaseResponse;
import com.shoppingmall.common.utils.ApiResponseUtils;
import com.shoppingmall.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

/**
 * Global Exception Handler
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(CartException.class)
    public ResponseEntity<BaseResponse<?>> handleCartException(CartException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(ex.getFailureCode());
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<BaseResponse<?>> handleCommentException(CommentException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(ex.getFailureCode());
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<BaseResponse<?>> handleFileException(FileException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(ex.getFailureCode());
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<BaseResponse<?>> handleMemberException(MemberException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(ex.getFailureCode());
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<BaseResponse<?>> handleMemberException(PostException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(ex.getFailureCode());
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<BaseResponse<?>> handleMemberException(ProductException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(ex.getFailureCode());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<?>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<BaseResponse<?>> handleInvalidParameterException(InvalidParameterException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(HttpStatus.BAD_REQUEST, "Required request parameter is missing: " + ex.getParameterName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(HttpStatus.BAD_REQUEST, "Validation error: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(HttpStatus.BAD_REQUEST, "Message not readable: " + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error(ex.getMessage());
        return ApiResponseUtils.failure(HttpStatus.FORBIDDEN, "Access denied: " + ex.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    protected ResponseEntity<BaseResponse<?>> handleSQLException(SQLException ex) {
        log.error("Database error occurred: {}", ex.getMessage());
        return ApiResponseUtils.failure(HttpStatus.INTERNAL_SERVER_ERROR, "Database error occurred. Please contact support.");
    }

    @ExceptionHandler({BadSqlGrammarException.class, DataAccessException.class, SQLSyntaxErrorException.class})
    protected ResponseEntity<BaseResponse<?>> handleSQLSyntaxErrorException(SQLException ex) {
        log.error("Database syntax error occurred: {}", ex.getMessage());
        return ApiResponseUtils.failure(HttpStatus.INTERNAL_SERVER_ERROR, "Database syntax error occurred. Please contact support.");
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<BaseResponse<?>> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ApiResponseUtils.failure(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
}
