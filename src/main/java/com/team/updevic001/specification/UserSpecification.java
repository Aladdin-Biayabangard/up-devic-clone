package com.team.updevic001.specification;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserRole;
import com.team.updevic001.model.dtos.response.user.UserResponseForAdmin;
import com.team.updevic001.model.enums.Status;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

public class UserSpecification {

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) ->
                firstName == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) ->
                lastName == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                email == null ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<User> hasStatus(Status status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null :
                        criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<User> hasRoles(List<String> roles) {
        return (root, query, criteriaBuilder) -> {
            if (roles == null || roles.isEmpty()) {
                return null;
            }
            Join<User, UserRole> roleJoin = root.join("roles", JoinType.LEFT);
            return roleJoin.get("name").in(roles);
        };
    }


    public static Specification<User> filter(UserCriteria user) {
        return Specification.where(hasFirstName(user.getFirstName()))
                .and(hasLastName(user.getLastName()))
                .and(hasEmail(user.getEmail()))
                .and(hasStatus(user.getStatus()))
                .and(hasRoles(user.getRoles()));
    }
}
