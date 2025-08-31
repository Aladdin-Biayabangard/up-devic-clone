package com.team.updevic001.domain.applications.repo;


import com.team.updevic001.domain.applications.domain.ApplicationStatus;
import com.team.updevic001.domain.applications.dto.ApplicationSearchDto;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CourseApplicationSpecification {

    public static Specification<CourseApplicationsEntity> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                email == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<CourseApplicationsEntity> hasFullName(String fullName) {
        return (root, query, criteriaBuilder) ->
                fullName == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<CourseApplicationsEntity> hasPhone(String phone) {
        return (root, query, criteriaBuilder) ->
                phone == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
    }

    public static Specification<CourseApplicationsEntity> hasMessage(String message) {
        return (root, query, criteriaBuilder) ->
                message == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.like(criteriaBuilder.lower(root.get("message")), "%" + message.toLowerCase() + "%");
    }

    public static Specification<CourseApplicationsEntity> createdAtAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                date == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<CourseApplicationsEntity> createdAtBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                date == null ? criteriaBuilder.conjunction()
                        : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<CourseApplicationsEntity> statusIn(ApplicationStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? criteriaBuilder.conjunction() : criteriaBuilder
                        .equal(root.get(CourseApplicationsEntity.Fields.status), status);
    }

    public static Specification<CourseApplicationsEntity> buildSpecification(ApplicationSearchDto dto) {
        return Specification
                .where(hasEmail(dto.getEmail()))
                .and(hasFullName(dto.getFullName()))
                .and(hasPhone(dto.getPhone()))
                .and(hasMessage(dto.getMessage()))
                .and(createdAtAfter(dto.getCreatedAtFrom()))
                .and(createdAtBefore(dto.getCreatedAtTo()))
                .and(statusIn(dto.getStatus()));
    }
}
