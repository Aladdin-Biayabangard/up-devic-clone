package com.team.updevic001.exceptions;

public class ExpiredRefreshTokenException extends RuntimeException {
    public ExpiredRefreshTokenException(String message) {
        super(message);
    }
}