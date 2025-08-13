package com.team.updevic001.dao.entities;

 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.updevic001.model.enums.Specialty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
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
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Specialty speciality;

    @Column(name = "experience_years")
    private Integer experienceYears;

    private BigDecimal balance;

    @Column(name = "hire_date")
    @CreationTimestamp
    private LocalDateTime hireDate;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    
}