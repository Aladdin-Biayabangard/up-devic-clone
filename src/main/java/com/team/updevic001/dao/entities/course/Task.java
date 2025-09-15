package com.team.updevic001.dao.entities.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.updevic001.dao.entities.StudentTask;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskNumbers;
    private String questions;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> options = new ArrayList<>();

    private String correctAnswer;

    @OneToMany(mappedBy = "task", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private List<StudentTask> studentTasks = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @PrePersist
    public void generatedId() {
        if (this.taskNumbers == null) {
            this.taskNumbers = UUID.randomUUID().toString().substring(0, 5);
        }
    }
}
