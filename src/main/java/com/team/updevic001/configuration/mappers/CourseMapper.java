package com.team.updevic001.configuration.mappers;

import com.team.updevic001.dao.entities.*;
import com.team.updevic001.dao.repositories.LessonRepository;
import com.team.updevic001.dao.repositories.StudentCourseRepository;
import com.team.updevic001.dao.repositories.TeacherCourseRepository;
import com.team.updevic001.model.dtos.response.course.ResponseCourseDto;
import com.team.updevic001.model.dtos.response.course.ResponseCourseShortInfoDto;
import com.team.updevic001.model.dtos.response.course.ResponseFullCourseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CourseMapper {

//    private final LessonMapper lessonMapper;
//    private final CommentMapper commentMapper;
    private final TeacherCourseRepository teacherCourseRepository;
    private final LessonRepository lessonRepository;
    private final StudentCourseRepository studentCourseRepository;


    public ResponseFullCourseDto toFullResponse(Course course) {
        return new ResponseFullCourseDto(
                course.getPhoto_url(),
                userFullName(course),
                getTeacherNames(course),
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getCreatedAt(),
                lessonCount(course),
                studentCount(course),
                teacherCount(course),
                course.getRating(),
//                lessonMapper.toShortLesson(lessons),
//                commentMapper.toDto(comments),
                course.getPrice()
        );
    }

    public ResponseCourseShortInfoDto toCourseResponse(Course course) {
        return new ResponseCourseShortInfoDto(
                course.getId(),
                course.getCourseCategoryType(),
                course.getPhoto_url(),
                userFullName(course),
                course.getTitle(),
                shortDescription(course),
                course.getLevel(),
                lessonCount(course),
                studentCount(course),
                course.getRating(),
                course.getPrice());
    }

    public ResponseCourseDto courseDto(Course course) {
        return new ResponseCourseDto(
                course.getId(),
                course.getCourseCategoryType(),
                course.getPhoto_url(),
                course.getTitle(),
                course.getDescription(),
                course.getLevel());
    }

    public List<ResponseCourseShortInfoDto> toCourseResponse(List<Course> courses) {
        return courses.stream().map(this::toCourseResponse).toList();
    }


    public List<String> getTeacherNames(Course course) {
        return teacherCourseRepository.findTeacherNamesByCourse(course).stream().
                map(teacherNameDto -> teacherNameDto.firstName() + " " + teacherNameDto.lastName())
                .filter(name -> !name.equals(course.getHeadTeacher())).toList();
    }

    private String shortDescription(Course course) {
        String description = course.getDescription();
        String[] split = description.split("\\.");
        return split[0];
    }

    private String userFullName(Course course) {
        User user = course.getHeadTeacher().getUser();
        return user.getFirstName() + " " + user.getLastName();
    }

    private int lessonCount(Course course) {
        return lessonRepository.findLessonByCourseId(course.getId()).size();
    }

    private int studentCount(Course course) {
        return studentCourseRepository.countStudentByCourse(course);
    }

    private int teacherCount(Course course) {
        return teacherCourseRepository.countTeacherByCourse(course);
    }

    public List<ResponseCourseDto> courseDto(List<Course> courses) {
        return courses.stream().map(this::courseDto).toList();
    }
}
