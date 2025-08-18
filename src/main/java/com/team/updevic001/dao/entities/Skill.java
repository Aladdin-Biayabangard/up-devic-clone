//package com.team.updevic001.dao.entities;
//
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@EqualsAndHashCode(of = "id")
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Getter
//@Setter
//@Entity
//@Table(name = "user_skills")
//public class Skill {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "skill", nullable = false)
//    private String name;
//
//    @ManyToOne
//    @JoinColumn(name = "user_profile_id", nullable = false)
//    private UserProfile userProfile;
//}
