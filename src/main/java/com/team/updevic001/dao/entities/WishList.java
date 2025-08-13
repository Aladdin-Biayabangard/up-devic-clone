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
@Table(name = "wish_list",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "course_id"})
        })
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
