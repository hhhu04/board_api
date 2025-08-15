package com.project.board.util.jwt;

import lombok.Data;

@Data
public class Token {
    private boolean isValidate;
    private String token;
    private String refreshToken;
    private boolean isRefresh = false;

    public Token(boolean isValidate) {
        this.isValidate = isValidate;
    }
}
