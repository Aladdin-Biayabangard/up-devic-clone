package com.team.updevic001.dao.entities;

import com.team.updevic001.model.dtos.certificate.Platform;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "certificate_views")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CertificateViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String certificateId;

    @Enumerated(STRING)
    private Platform platform;

    private Long viewCount;
}
