package io.hypr.identityservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1000, "Uncategorized exception"),
    USER_EXISTED(1001, "User existed"),
    USERNAME_INVALID(1002, "Username can't be blank and must have at least 6 characters"),
    PASSWORD_INVALID(1003, "Password must have at least 8 characters with uppercase & lowercase characters, numbers and 1 special character"),
    ;

    private final int code;
    private final String message;
}
