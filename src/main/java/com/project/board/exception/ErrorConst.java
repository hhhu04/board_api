package com.project.board.exception;

import lombok.Getter;

@Getter
public enum ErrorConst {

    REQUIRED_AUTH(-1000, "인증이 필요합니다. 로그인해 주세요."),
    EXPRIED_TOKEN(-1001, "토큰이 만료되었습니다. 다시 로그인해 주세요."),
    INVALID_TOKEN(-1002, "잘못된 토큰입니다. 다시 로그인해 주세요."),
    WRONG_AUTH(-1004, "인증값이 잘못되었습니다."),

    UNKNOWN_ERROR(-5000, "오류가 발생했습니다. 다시 시도해주세요.");

    private int code;
    private String message;

    private ErrorConst(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessageByCode(int code) {

        for (ErrorConst e : ErrorConst.values()) {
            if (e.code == code) {
                return e.message;
            }
        }
        return null;
    }

}
