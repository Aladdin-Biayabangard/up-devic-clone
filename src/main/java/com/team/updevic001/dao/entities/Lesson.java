package com.team.updevic001.dao.entities;

 
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.team.updevic001.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "duration_formatted")
    private String duration;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    @Column(name = "photo_key")
    private String photoKey;

    @Column(name = "video_key")
    private String videoKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private Course course;
//
//    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<Comment> comments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "lesson", orphanRemoval = true, cascade = CascadeType.ALL)
//    private List<UserLessonStatus> userLessonStatuses = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Teacher teacher;


    

}