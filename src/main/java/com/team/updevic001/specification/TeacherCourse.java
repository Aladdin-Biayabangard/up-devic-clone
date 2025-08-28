//package com.team.updevic001.dao.entities;
//
//
//import com.team.updevic001.model.enums.TeacherPrivileges;
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.time.LocalDateTime;
//
//@EqualsAndHashCode(of = "id")
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Getter
//@Setter
//@Entity
//@Table(name = "teacher_courses")
//public class TeacherCourse {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "teacher_id", nullable = false)
//    private Teacher teacher;
//
//    @ManyToOne
//    @JoinColumn(name = "course_id", nullable = false)
//    private Course course;
//
//    @CreationTimestamp
//    private LocalDateTime assignedAt;
//
//    @Enumerated(EnumType.STRING)
//    private TeacherPrivileges teacherPrivilege;
//
//
//
//}
