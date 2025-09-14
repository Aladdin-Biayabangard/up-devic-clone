package com.team.updevic001.dao.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
@Builder
@Getter
@Setter
@Entity
@Table(name = "teacher_balances")
public class TeachersBalance {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "teacher_id", unique = true)
    User teacher;

    @Builder.Default
    BigDecimal balance = BigDecimal.ZERO;


}
