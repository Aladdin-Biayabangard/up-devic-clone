package com.team.updevic001.dao.entities;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "course_id"})
        }
)
public class UserCourseFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private boolean payment;

}
