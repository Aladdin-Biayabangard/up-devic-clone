package com.team.updevic001.exceptions;

public record ErrorResponse(
            int status,
        String message,
        String details,
        String errorTime) {

}