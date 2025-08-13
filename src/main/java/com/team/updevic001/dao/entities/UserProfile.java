package com.team.updevic001.dao.entities;

 
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "profile_photo_url", columnDefinition = "TEXT")
    private String profilePhoto_url;

    @Column(name = "profile_photo_key")
    private String profilePhotoKey;

    @Column(name = "bio", length = 500)
    private String bio;

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SocialLink> socialLinks = new HashSet<>();

    @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Skill> skills = new HashSet<>();



    
}
