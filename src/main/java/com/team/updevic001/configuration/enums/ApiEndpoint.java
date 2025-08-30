package com.team.updevic001.configuration.enums;

import org.springframework.http.HttpMethod;

import static com.team.updevic001.configuration.enums.ApiSecurityLevel.*;

public enum ApiEndpoint {

    // --- Public (Permit All) ---
    AUTH_PUBLIC("/v1/auth/**",null, PUBLIC),
    SWAGGER_V2("/v2/api-docs",null ,PUBLIC),
    SWAGGER_V3_ALL("/v3/api-docs/**", null,PUBLIC),
    SWAGGER_V3("/v3/api-docs",null, PUBLIC),
    SWAGGER_RESOURCES("/swagger-resources",null, PUBLIC),
    SWAGGER_RESOURCES_ALL("/swagger-resources/**",null, PUBLIC),
    SWAGGER_UI_CONFIG("/configuration/ui",null, PUBLIC),
    SWAGGER_UI("/swagger-ui/**",null, PUBLIC),
    ERROR_ENDPOINT("/error",null, PUBLIC),
    SWAGGER_UI_HTML("/swagger-ui.html",null, PUBLIC),

// --- Admin Controller ---
    ADMIN_ASSIGN_TEACHER("/api/v1/admins/assign/*", HttpMethod.POST, ADMIN),
    ADMIN_ACTIVATE_USER("/api/v1/admins/users/*/activate", HttpMethod.PUT, ADMIN),
    ADMIN_DEACTIVATE_USER("/api/v1/admins/users/*/deactivate", HttpMethod.PUT, ADMIN),
    ADMIN_ASSIGN_ROLE("/api/v1/admins/users/*/assign/role", HttpMethod.PUT, ADMIN),
    ADMIN_GET_ALL_USERS("/api/v1/admins/search", HttpMethod.GET, ADMIN),
    ADMIN_GET_USERS_COUNT("/api/v1/admins/users/count", HttpMethod.GET, ADMIN),
    ADMIN_REMOVE_ROLE("/api/v1/admins/users/*/role", HttpMethod.PUT, ADMIN),
    ADMIN_DELETE_USER("/api/v1/admins/users/*", HttpMethod.DELETE, ADMIN),
    // Auth Controller

    AUTH_CREATE_ADMIN("/api/v1/auth/create-admin", HttpMethod.PUT, PUBLIC),
    AUTH_GET_LOGGED_IN_USER("/api/v1/auth", HttpMethod.GET, PUBLIC),
    AUTH_SIGN_UP("/api/v1/auth/sign-up", HttpMethod.POST, PUBLIC),
    AUTH_VERIFY_OTP("/api/v1/auth/verify-otp", HttpMethod.POST, PUBLIC),
    AUTH_SIGN_IN("/api/v1/auth/sign-in", HttpMethod.POST, PUBLIC),
    AUTH_FORGOT_PASSWORD("/api/v1/auth/forgot-password", HttpMethod.POST, PUBLIC),
    AUTH_RESET_PASSWORD("/api/v1/auth/reset-password", HttpMethod.PATCH, STUDENT),
    AUTH_REFRESH_TOKEN("/api/v1/auth/refresh-token", HttpMethod.POST, PUBLIC),

    // Certificate Controller
        CERTIFICATE_DOWNLOAD("/api/certificate/download", HttpMethod.GET, STUDENT),

    // Comment Controller
    COMMENT_ADD_TO_COURSE("/api/v1/comments/courses/*", HttpMethod.POST, STUDENT),
    COMMENT_ADD_TO_LESSON("/api/v1/comments/lessons/*", HttpMethod.POST, STUDENT),
    COMMENT_UPDATE("/api/v1/comments/*", HttpMethod.PUT, STUDENT),
    COMMENT_GET_COURSE("/api/v1/comments/courses/*", HttpMethod.GET, STUDENT),
    COMMENT_GET_LESSON("/api/v1/comments/lessons/*", HttpMethod.GET, STUDENT),
    COMMENT_DELETE("/api/v1/comments/*", HttpMethod.DELETE, STUDENT),

    // Course Controller
    COURSE_CREATE("/api/v1/courses", HttpMethod.POST, TEACHER),
    COURSE_UPDATE("/api/v1/courses/*", HttpMethod.PUT, TEACHER),
    COURSE_UPLOAD_PHOTO("/api/v1/courses/*/photo", HttpMethod.PATCH, TEACHER),
    COURSE_DELETE("/api/v1/courses/*", HttpMethod.DELETE, TEACHER),

    COURSE_ADD_WISH("/api/v1/courses/*/wish", HttpMethod.POST, STUDENT),
    COURSE_REMOVE_WISH("/api/v1/courses/*/wish", HttpMethod.DELETE, STUDENT),
    COURSE_GET_WISHLIST("/api/v1/courses/wish", HttpMethod.GET, STUDENT),

    COURSE_GET_ONE("/api/v1/courses/*", HttpMethod.GET, PUBLIC),
    COURSE_SEARCH("/api/v1/courses/search", HttpMethod.GET, PUBLIC),
    COURSE_GET_CATEGORIES("/api/v1/courses/categories", HttpMethod.GET, PUBLIC),
    COURSE_GET_POPULAR("/api/v1/courses/popular-courses", HttpMethod.GET, PUBLIC),
    COURSE_UPDATE_RATING("/api/v1/courses/*/rating", HttpMethod.PATCH, PUBLIC),

    // --- Lesson Controller ---
    LESSON_ASSIGN_TO_COURSE("/api/v1/lessons/courses/*", HttpMethod.POST, TEACHER),
    LESSON_UPDATE_INFO("/api/v1/lessons/*", HttpMethod.PUT, TEACHER),
    LESSON_UPLOAD_PHOTO("/api/v1/lessons/*/photo", HttpMethod.PATCH, TEACHER),
    LESSON_DELETE("/api/v1/lessons/*", HttpMethod.DELETE, TEACHER),

    LESSON_GET_BY_COURSE("/api/v1/lessons/courses/*", HttpMethod.GET, STUDENT),
    LESSON_GET_FULL("/api/v1/lessons/*", HttpMethod.GET, STUDENT),

    // --- Payment Controller ---
    PAYMENT_SUCCESS("/api/v1/payment/success", HttpMethod.GET, STUDENT),
    PAYMENT_CANCEL("/api/v1/payment/cancel", HttpMethod.GET, STUDENT),
    PAYMENT_CHECKOUT("/api/v1/payment", HttpMethod.POST, STUDENT),
    PAYMENT_TEACHER_BALANCE("/api/v1/payment/balance", HttpMethod.GET, STUDENT),

    // --- Student Controller ---
    STUDENT_UNENROLL("/api/v1/students/unenroll", HttpMethod.DELETE, STUDENT),
    STUDENT_GET_COURSE("/api/v1/students", HttpMethod.GET, STUDENT),
    STUDENT_GET_COURSES("/api/v1/students/courses", HttpMethod.GET, STUDENT),
    STUDENT_REQUEST_TO_BECOME_TEACHER("/api/v1/students/for-teacher", HttpMethod.GET, STUDENT),

    // --- Task Controller ---
    TASK_CREATE("/api/v1/tasks/courses/*", HttpMethod.POST, TEACHER),
    TASK_CHECK_ANSWER("/api/v1/tasks/*/courses/*", HttpMethod.POST, STUDENT),
    TASK_GET_TASKS("/api/v1/tasks/courses/*", HttpMethod.GET, STUDENT),

    // --- Teacher Controller ---
    TEACHER_GET_COURSES("/api/teacher/courses", HttpMethod.GET, TEACHER),
    TEACHER_GET_MAIN_INFO("/api/teacher/info", HttpMethod.GET, PUBLIC),
    TEACHER_GET_PROFILE("/api/teacher/*/profile", HttpMethod.GET, STUDENT),
    TEACHER_GET_SHORT_INFO("/api/teacher/*/info", HttpMethod.GET, STUDENT),
    TEACHER_SEARCH("/api/teacher/search", HttpMethod.GET, STUDENT),

    USER_UPDATE_PROFILE("/api/users", HttpMethod.PUT, STUDENT),
    USER_UPDATE_PASSWORD("/api/users/password", HttpMethod.PATCH, STUDENT),
    USER_UPLOAD_PHOTO("/api/users/photo", HttpMethod.PATCH, STUDENT),
    USER_GET_PROFILE("/api/users/profile", HttpMethod.GET, STUDENT),
    USER_GET_BY_ID("/api/users/*", HttpMethod.GET, STUDENT);


    private final String pathPattern;
    private final HttpMethod httpMethod;
    private final ApiSecurityLevel securityLevel;

    ApiEndpoint(String pathPattern, HttpMethod httpMethod, ApiSecurityLevel securityLevel) {
        this.pathPattern = pathPattern;
        this.httpMethod = httpMethod;
        this.securityLevel = securityLevel;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public ApiSecurityLevel getSecurityLevel() {
        return securityLevel;
    }
}
