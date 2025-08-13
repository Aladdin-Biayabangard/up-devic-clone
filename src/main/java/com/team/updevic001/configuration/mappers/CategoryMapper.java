package com.team.updevic001.configuration.mappers;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.repositories.CourseRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Data
public class CategoryMapper {

    private final CourseMapper courseMapper;
    private final ModelMapper modelMapper;
    private final CourseRepository courseRepository;

//
//    public ResponseCategoryDto toDto(List<Course> courses) {
//        return new ResponseCategoryDto(
//                courses.getFirst().getCourseCategoryType(),
//                courses.size()
//        );
//    }
//
//    public List<ResponseCategoryDto> toDto(List<Course> courses){
//        return courses.stream(
//    }
//

    private Integer courseCount(Course course) {
        List<Course> courses = courseRepository.findCourseByCourseCategoryType(course.getCourseCategoryType());
        return !courses.isEmpty() ? courses.size() : 0;
    }
}
