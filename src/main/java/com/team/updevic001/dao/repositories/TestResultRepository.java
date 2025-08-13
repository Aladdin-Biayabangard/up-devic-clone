package com.team.updevic001.dao.repositories;

import com.team.updevic001.dao.entities.Course;
import com.team.updevic001.dao.entities.TestResult;
import com.team.updevic001.dao.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    Optional<TestResult> findTestResultByStudentAndCourse(User student, Course course);

    void deleteAllByCourseId(Long courseId);

}
