package com.team.updevic001.specification;

import com.team.updevic001.dao.entities.course.Course;
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.CourseLevel;
import com.team.updevic001.specification.criteria.CourseSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecification {

    public static Specification<Course> hasTitle(String title) {
        return (root, query, cb) ->
                (title == null || title.isBlank())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get(Course.Fields.title)), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Course> hasLevel(CourseLevel level) {
        return (root, query, cb) ->
                level == null
                        ? cb.conjunction()
                        : cb.equal(root.get(Course.Fields.level), level);
    }

    public static Specification<Course> hasMinPrice(Double minPrice) {
        return (root, query, cb) ->
                minPrice == null
                        ? cb.conjunction()
                        : cb.greaterThanOrEqualTo(root.get(Course.Fields.price), minPrice);
    }

    public static Specification<Course> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null
                        ? cb.conjunction()
                        : cb.lessThanOrEqualTo(root.get(Course.Fields.price), maxPrice);
    }

    public static Specification<Course> hasCategory(CourseCategoryType categoryType) {
        return (root, query, cb) ->
                categoryType == null
                        ? cb.conjunction()
                        : cb.equal(root.get(Course.Fields.courseCategoryType), categoryType);
    }

    public static Specification<Course> buildSpecification(CourseSearchCriteria criteria) {
        return Specification
                .where(hasTitle(criteria.getName()))
                .and(hasLevel(criteria.getLevel()))
                .and(hasMinPrice(criteria.getMinPrice()))
                .and(hasMaxPrice(criteria.getMaxPrice()))
                .and(hasCategory(criteria.getCourseCategoryType()));
    }
}
