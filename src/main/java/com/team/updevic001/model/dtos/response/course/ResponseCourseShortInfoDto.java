package com.team.updevic001.model.dtos.response.course;

import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCourseShortInfoDto {

    private String courseId;

    private CourseCategoryType category;

    private String photo_url;

    private String headTeacher;

    private String title;

    private String description;

    private CourseLevel level;

    private long lessonCount;

    private long studentCount;

    private double rating;

    private double price;

}
