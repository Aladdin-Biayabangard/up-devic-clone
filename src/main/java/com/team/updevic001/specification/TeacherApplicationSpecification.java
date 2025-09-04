package com.team.updevic001.specification;

import com.team.updevic001.dao.entities.TeacherApplicationsEntity;
import com.team.updevic001.model.dtos.application.ApplicationSearchDto;
import com.team.updevic001.model.enums.ApplicationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TeacherApplicationSpecification {

    public static Specification<TeacherApplicationsEntity> hasEmail(String email) {
        return (root, query, cb) ->
                email == null || email.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get(TeacherApplicationsEntity.Fields.email)), "%" + email.toLowerCase() + "%");
    }

    public static Specification<TeacherApplicationsEntity> hasFullName(String fullName) {
        return (root, query, cb) ->
                fullName == null || fullName.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get(TeacherApplicationsEntity.Fields.fullName)), "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<TeacherApplicationsEntity> hasTeachingField(String teachingField) {
        return (root, query, cb) ->
                teachingField == null || teachingField.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get(TeacherApplicationsEntity.Fields.teachingField)), "%" + teachingField.toLowerCase() + "%");
    }

    public static Specification<TeacherApplicationsEntity> hasPhone(String phone) {
        return (root, query, cb) ->
                phone == null || phone.isBlank()
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get(TeacherApplicationsEntity.Fields.phoneNumber)), "%" + phone.toLowerCase() + "%");
    }

    public static Specification<TeacherApplicationsEntity> createdAtAfter(LocalDateTime date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get(TeacherApplicationsEntity.Fields.createdAt), date);
    }

    public static Specification<TeacherApplicationsEntity> createdAtBefore(LocalDateTime date) {
        return (root, query, cb) ->
                date == null ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get(TeacherApplicationsEntity.Fields.createdAt), date);
    }

    public static Specification<TeacherApplicationsEntity> hasStatus(ApplicationStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction()
                        : cb.equal(root.get(TeacherApplicationsEntity.Fields.status), status);
    }

    public static Specification<TeacherApplicationsEntity> buildSpecification(ApplicationSearchDto dto) {
        return Specification
                .where(hasEmail(dto.getEmail()))
                .and(hasFullName(dto.getFullName()))
                .and(hasTeachingField(dto.getTeachingField()))
                .and(hasPhone(dto.getPhone()))
                .and(createdAtAfter(dto.getCreatedAtFrom()))
                .and(createdAtBefore(dto.getCreatedAtTo()))
                .and(hasStatus(dto.getStatus()));
    }
}
