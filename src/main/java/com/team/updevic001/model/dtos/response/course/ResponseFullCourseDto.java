package com.team.updevic001.model.dtos.response.course;


import com.team.updevic001.model.enums.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFullCourseDto {

    private String photo_url;

    private Long teacher;

    private String title;

    private String description;

    private CourseLevel level;

    private LocalDateTime createdAt;

    private long lessonCount;

    private long studentCount;

    private double rating;

    private double price;

}
