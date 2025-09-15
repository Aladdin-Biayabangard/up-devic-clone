package com.team.updevic001.dao.entities;


import com.team.updevic001.dao.entities.auth.User;
import com.team.updevic001.dao.entities.course.Task;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "student_tasks")
public class StudentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    private String correctAnswer;

    private String studentAnswer;

    private String feedback;

    private Boolean completed;

    private boolean correct;

    private boolean submitted;

    private double score;
}
