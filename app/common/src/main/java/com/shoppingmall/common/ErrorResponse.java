package com.shoppingmall.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private LocalDateTime timestamp = LocalDateTime.now();
    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("errors")
    private List<CustomFieldError> customFieldErrors = new ArrayList<>();

    public static ErrorResponse create() {
        return new ErrorResponse();
    }

    public ErrorResponse code(int code) {
        this.code = code;
        return this;
    }

    public ErrorResponse message(String message) {
        this.message = message;
        return this;
    }

    public ErrorResponse errors(Errors errors) {
        addCustomFieldErrors(errors.getFieldErrors());
        return this;
    }

    public void addCustomFieldErrors(List<FieldError> fieldErrors) {
        fieldErrors.forEach(error -> {
            customFieldErrors.add(
                    new CustomFieldError(Objects.requireNonNull(error.getCodes())[0],
                            error.getRejectedValue(),
                            error.getDefaultMessage())
            );
        });
    }

    // Used when parameter validation fails
    @Getter
    @RequiredArgsConstructor
    public static class CustomFieldError {
        private final String field;
        private final Object value;
        private final String reason;
    }
}