package com.team.updevic001.specification;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.CourseLevel;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CourseSpecification {

    public static Specification<Course> hasLevel(CourseLevel level) {
        return (root, query, criteriaBuilder) ->
                level == null ? null : criteriaBuilder.equal(root.get("level"), level);
    }

    public static Specification<Course> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) ->
                minPrice == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Course> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) ->
                maxPrice == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Course> hasCategory(CourseCategoryType type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("courseCategoryType"), type);
    }
}
