package io.hypr.identityservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1000, "Uncategorized exception"),
    USER_EXISTED(1001, "User existed"),
    USER_NOT_FOUND(1002, "User not found"),
    USERNAME_INVALID(1003, "Username can't be blank and must have at least 6 characters"),
    PASSWORD_INVALID(1004, "Password must have at least 8 characters with uppercase & lowercase characters, numbers and 1 special character"),
    UNAUTHENTICATED(1005, "Unauthenticated"),
    ;

    private final int code;
    private final String message;
}
