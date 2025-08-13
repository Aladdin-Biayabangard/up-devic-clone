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
@Table(name = "user_social_links")
public class SocialLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social_link", nullable = false)
    private String link;

    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
}
