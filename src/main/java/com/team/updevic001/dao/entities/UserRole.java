package com.team.updevic001.dao.entities;


import com.team.updevic001.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(unique = true, nullable = false)
    private Role name;

}
