package com.team.updevic001.criteria;

import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseSearchCriteria {
    private String name;
    private String email;
    private CourseLevel level;
    private Double minPrice;
    private Double maxPrice;
    private CourseCategoryType courseCategoryType;
}
