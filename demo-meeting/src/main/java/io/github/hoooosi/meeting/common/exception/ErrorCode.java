package io.github.hoooosi.meeting.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(20000, "Ok"),

    CHECK_CODE_EXPIRED(40001, "CHECK_CODE_EXPIRED"),
    CHECK_CODE_ERROR(40002, "CHECK_CODE_ERROR"),
    EMAIL_EXISTS(40003, "EMAIL_EXISTS"),
    USER_NOT_FOUND(40004, "USER_NOT_FOUND"),
    PASSWORD_ERROR(40005, "PASSWORD_ERROR"),
    USER_FORBIDDEN(40006, "USER_FORBIDDEN"),
    UNAUTHORIZED(40100, "UNAUTHORIZED"),
    TOKEN_INVALID(40101, "TOKEN_INVALID"),
    ALREADY_LOGIN(40102, "ALREADY_LOGIN"),
    FORBIDDEN(40300, "FORBIDDEN"),

    DATA_SAVE_ERROR(50001, "DATA_SAVE_ERROR");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}