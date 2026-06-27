package com.codex.learning.order.api.error;

public enum ErrorCode {
    INVALID_REQUEST(400),
    ORDER_NOT_FOUND(404),
    ORDER_STATUS_ILLEGAL(409);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
