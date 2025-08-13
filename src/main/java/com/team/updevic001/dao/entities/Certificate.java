package com.team.updevic001.dao.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_content")
    private String certificateContent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    @Column(name = "issue_date")
    @CreationTimestamp
    private LocalDateTime issueDate;

}
