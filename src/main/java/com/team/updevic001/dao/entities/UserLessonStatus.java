package com.team.updevic001.dao.entities;

 
import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.course.Lesson;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "user_lesson_status")
public class UserLessonStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "is_watched")
    @Builder.Default
    private boolean isWatched = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    
}
