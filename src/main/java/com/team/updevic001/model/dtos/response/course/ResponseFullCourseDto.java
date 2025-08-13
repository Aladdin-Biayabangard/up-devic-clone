package com.team.updevic001.model.dtos.response.course;

import com.team.updevic001.model.dtos.response.comment.ResponseCommentDto;
import com.team.updevic001.model.dtos.response.lesson.ResponseLessonShortInfoDto;
import com.team.updevic001.model.enums.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFullCourseDto {

    private String photo_url;

    private String headTeacher;

    private List<String> teachers;

    private String title;

    private String description;

    private CourseLevel level;

    private LocalDateTime createdAt;

    private long lessonCount;

    private long studentCount;

    private long teacherCount;

    private double rating;

    private List<ResponseLessonShortInfoDto> lessons;

    private List<ResponseCommentDto> comments;

    private double price;

}
