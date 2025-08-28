package com.team.updevic001.dao.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer code;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    // OTP istifadə olundumu?
    @Column(nullable = false)
    private boolean used = false;

    // OTP neçə dəfə göndərilib?
    @Column(nullable = false)
    private int retryCount = 0;

    // Son göndərilmə vaxtı
    private LocalDateTime lastSentTime;

    // OTP yaradılma vaxtı
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
