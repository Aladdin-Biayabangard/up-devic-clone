package com.team.updevic001.exceptions;

import lombok.Getter;

@Getter
public class ExpiredRefreshTokenException extends RuntimeException {

    private String code;

    public ExpiredRefreshTokenException(String code, String message) {
        super(message);
        this.code=code;
    }
}