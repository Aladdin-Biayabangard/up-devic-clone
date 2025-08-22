package com.team.updevic001.exceptions;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    private String code;
    public UnauthorizedException(String code,String message) {
        super(message);
        this.code=code;
    }
}
