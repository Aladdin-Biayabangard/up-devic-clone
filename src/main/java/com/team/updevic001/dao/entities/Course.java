package com.team.updevic001.dao.entities;

 
import com.team.updevic001.model.enums.CourseCategoryType;
import com.team.updevic001.model.enums.CourseLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CourseCategoryType courseCategoryType;

    @ManyToOne
    private Teacher headTeacher;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "course_level")
    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @Column(name = "rating")
    private double rating = 0;

    @Column(name = "price")
    private Double price;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photo_url;

    @Column(name = "photo_key")
    private String photoKey;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "certificate_id", referencedColumnName = "id")
    private Certificate certificate;


    // Bir çox TeacherCourse ilə əlaqə
//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<TeacherCourse> teacherCourses = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserCourseFee> userCourseFees;
//
//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TestResult> testResults = new ArrayList<>();
//
//    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CourseRating> courseRatings;


}
