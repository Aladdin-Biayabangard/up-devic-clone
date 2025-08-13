package com.team.updevic001.configuration.enums;

import com.team.updevic001.model.enums.Role;
import lombok.Getter;

@Getter
public enum AuthMapping {

    TEACHER_ADMIN_DELETE(new String[]{Role.TEACHER.name(), Role.ADMIN.name()}, new String[]{
            "/api/course/{courseId}",//delete elemek ucun
            "/api/lessons/{lessonId}",//delete
            "/api/lessons/lessons/delete",//delete
            "/api/teacher/{userId}"//delete
    }),

    ADMIN(new String[]{Role.ADMIN.name()}, new String[]{
            "/api/admin/**",
            "/api/teacher/delete/all"
    }),

    PERMIT_ALL_GET(null, new String[]{
            "/api/course/search",
            "/api/payment/test",
            "/api/course/criteria/**",
            "/api/course/sort/**",
            "/api/course/{courseId}",
            "/api/course/all",
            "api/course/categories",
            "api/course/short?categoryType=",
            "api/course/{courseId}/full",
            "/api/course/category",
            "/api/lesson/{courseId}/lessons",
            "api/lesson/{courseId}/lesson-short",
            "/api/lessons/teacher-lessons",
            "/api/teacher/{teacherId}/courses",
            "/api/comment/{courseId}/course",
            "/api/comment/{lessonId}/lesson",
            "/api/users/search"
    }),

    PERMIT_ALL(null, new String[]{
            "/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/auth/**",
            "/error"
    });

    private final String[] role;
    private final String[] urls;

    AuthMapping(String[] role, String[] urls) {
        this.role = role;
        this.urls = urls;
    }

}
