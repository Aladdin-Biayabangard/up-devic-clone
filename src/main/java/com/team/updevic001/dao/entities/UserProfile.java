package com.team.updevic001.dao.entities;


import com.team.updevic001.model.enums.Specialty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "profile_photo_url", columnDefinition = "TEXT")
    private String profilePhotoUrl;

    @Column(name = "profile_photo_key")
    private String profilePhotoKey;

    @Column(name = "bio", length = 500)
    private String bio;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "social_link")
    private Set<String> socialLinks = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    private String speciality;

    @Column(name = "experience_years")
    private Integer experienceYears;

    private BigDecimal balance;

    @CreationTimestamp
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
}
