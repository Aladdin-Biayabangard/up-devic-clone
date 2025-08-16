package com.team.updevic001.specification;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.Course.Fields;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.CourseLevel;
import org.springframework.data.jpa.domain.Specification;


public class CourseSpecification {

    public static Specification<Course> hasLevel(CourseLevel level) {
        return (root, query, cb) ->
                level == null ? cb.conjunction() : cb.equal(root.get(Fields.level), level);
    }

    public static Specification<Course> priceGreaterThanOrEqual(Double minPrice) {
        return (root, query, cb) ->
                minPrice == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get(Fields.price), minPrice);
    }

    public static Specification<Course> priceLessThanOrEqual(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get(Fields.price), maxPrice);
    }

    public static Specification<Course> hasCategory(CourseCategoryType type) {
        return (root, query, cb) ->
                type == null ? cb.conjunction() : cb.equal(root.get(Fields.courseCategoryType), type);
    }

    public static Specification<Course> hasName(String name) {
        return (root, query, cb) ->
                (name == null || name.isEmpty())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get(Course.Fields.title)), "%" + name.toLowerCase() + "%");
    }

}
