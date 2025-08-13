package com.team.updevic001.model.enums;

import java.util.Set;

public enum TeacherPrivileges {
    HEAD_TEACHER(Set.of(TeacherPermission.DELETE_COURSE, TeacherPermission.DELETE_LESSON, TeacherPermission.ADD_TEACHER, TeacherPermission.ADD_LESSON)),
    ASSISTANT_TEACHER(Set.of(TeacherPermission.ADD_LESSON));

    TeacherPrivileges(Set<TeacherPermission> permissions) {
        this.permissions = permissions;
    }

    private final Set<TeacherPermission> permissions;

    public boolean hasPermission(TeacherPermission permission) {
        return permissions.contains(permission);
    }


}