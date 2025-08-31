package com.team.updevic001.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionConstants {

    UNEXPECTED_EXCEPTION("UNEXPECTED_EXCEPTION", "Unexpected exception with method: %s"),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found with email: %s"),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "Role not found :  %s"),
    ADMIN_NOT_FOUND("ADMIN_NOT_FOUND", "Admin not found with id: %s"),
    TEACHER_NOT_FOUND("TEACHER_NOT_FOUND", "Teacher not found with id: %s"),
    CERTIFICATE_NOT_FOUND("CERTIFICATE_NOT_FOUND", "Certificate not found with id: %s"),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "Comment not found with id: %s"),
    COURSE_NOT_FOUND("COURSE_NOT_FOUND", "Course not found with id: %s"),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "File not found with key: %s"),
    LESSON_NOT_FOUND("LESSON_NOT_FOUND", "Lesson not found with id: %s"),
    OTP_NOT_FOUND("OTP_NOT_FOUND", "Otp not found with id: %s"),
    TASK_NOT_FOUND("TASK_NOT_FOUND", "Task not found with id: %s"),
    APPLICATION_NOT_FOUND("APPLICATION_NOT_FOUND", "Application not found with id: %s"),
    NOT_FOUND("NOT_FOUND", "%s"),


    UNAUTHORIZED_EXCEPTION("UNAUTHORIZED_EXCEPTION", "No authenticated user found with id: %s"),
    FORBIDDEN_EXCEPTION("FORBIDDEN_EXCEPTION", "Not allowed with id: %s"),

    ALREADY_EXISTS_EXCEPTION("ALREADY_EXISTS_EXCEPTION", "Resource already exists with value: %s"),
    EXPIRED_REFRESH_TOKEN_EXCEPTION("EXPIRED_REFRESH_TOKEN_EXCEPTION", "Refresh token expired at: %s");

    private String code;
    private String message;
}
