package com.team.updevic001.configuration.enums;

import com.team.updevic001.model.enums.Role;
import lombok.Getter;

@Getter
public enum AuthMapping {

    // Teacher və Admin yalnız delete əməliyyatları icra edə biləcək
    TEACHER_ADMIN_DELETE(new String[]{Role.TEACHER.name(), Role.ADMIN.name()}, new String[]{
            "/api/v1/course/{courseId}",
            "api/v1/course/**",
            "/api/v1/lessons/{lessonId}",        // lesson delete
            "/api/v1/teacher/{userId}"           // teacher delete
    }),

    // Admin xüsusi əməliyyatlar
    ADMIN(new String[]{Role.ADMIN.name()}, new String[]{
            "/api/v1/admin/**",
            "/api/v1/teacher/delete/all"
    }),

    STUDENT(new String[]{Role.STUDENT.name()}, new String[]{
            "/api/v1/students/**",
            "/api/v1/task"
    }),

    // HEAD_TEACHER privileges
    HEAD_TEACHER(null, new String[]{
            "/api/v1/course/{courseId}",          // delete
            "/api/v1/course/{courseId}/teachers/{userId}", // add teacher
            "/api/v1/lessons/courses/{courseId}", // add lesson
            "/api/v1/lessons/{lessonId}",         // delete lesson
            "/api/v1/course/{courseId}/photo"
    }),

    // ASSISTANT_TEACHER privileges
    ASSISTANT_TEACHER(null, new String[]{
            "/api/v1/lessons/courses/{courseId}", // add lesson only
    }),

    // GET əməliyyatları hər kəsə açıq
    PERMIT_ALL_GET(null, new String[]{
            "/api/v1/course/search",
            "/api/v1/payment/test",
            "/api/v1/course/criteria/**",
            "/api/v1/course/sort/**",
            "/api/v1/course/{courseId}",
            "/api/v1/course/all",
            "/api/v1/course/categories",
            "/api/v1/course/short?categoryType=",
            "/api/v1/course/{courseId}/full",
            "/api/v1/course/category",
            "/api/v1/lesson/{courseId}/lessons",
            "/api/v1/lesson/{courseId}/lesson-short",
            "/api/v1/lessons/teacher-lessons",
            "/api/v1/teacher/{teacherId}/courses",
            "/api/v1/comment/{courseId}/course",
            "/api/v1/comment/{lessonId}/lesson",
            "/api/v1/users/search"
    }),

    // Auth, Swagger və error
    PERMIT_ALL(null, new String[]{
            "/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/error"
    });

    private final String[] role;
    private final String[] urls;

    AuthMapping(String[] role, String[] urls) {
        this.role = role;
        this.urls = urls;
    }

}
