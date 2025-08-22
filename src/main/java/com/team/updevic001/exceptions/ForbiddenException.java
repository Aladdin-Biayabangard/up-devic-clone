package com.team.updevic001.exceptions;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    private String code;
    public ForbiddenException(String code,String message) {
        super(message);
        this.code=code;
    }
}
