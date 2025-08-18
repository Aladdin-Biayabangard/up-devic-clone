package com.team.updevic001.configuration.mappers;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.Teacher;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherDto;
import com.team.updevic001.model.dtos.response.teacher.ResponseTeacherWithCourses;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TeacherMapper {

    private final CourseMapper courseMapper;


//    public ResponseTeacherWithCourses toDto(Teacher teacher, List<Course> course) {
//        return new ResponseTeacherWithCourses(
//                teacher.getUser().getFirstName(),
//                teacher.getUser().getLastName(),
//                teacher.getSpeciality(),
//                teacher.getExperienceYears(),
//                courseMapper.courseDto(course)
//        );
//    }

    public ResponseTeacherDto toTeacherDto(Teacher teacher, Set<String> socialLinks) {
        return new ResponseTeacherDto(
                teacher.getUser().getFirstName(),
                teacher.getUser().getLastName(),
                teacher.getUser().getEmail(),
                teacher.getSpeciality(),
                teacher.getExperienceYears(),
                socialLinks,
                teacher.getHireDate()
        );
    }
//
//    public List<ResponseTeacherDto> toTeacherDto(Map<Teacher, UserProfile> teacherUserProfile,
//                                                 Map<UserProfile, Set<String>> socialLinks) {
//        return teacherUserProfile.entrySet()
//                .stream()
//                .map(entry -> {
//                    Teacher teacher = entry.getKey();
//                    UserProfile profile = entry.getValue();
//                    List<String> links = socialLinks.getOrDefault(profile, List.of());
//                    return toTeacherDto(teacher, links);
//                })
//                .toList();
//    }


}
