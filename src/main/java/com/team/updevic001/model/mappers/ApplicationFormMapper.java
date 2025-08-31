package com.team.updevic001.model.mappers;

import com.team.updevic001.dao.entities.TeacherApplicationsEntity;
import com.team.updevic001.model.dtos.application.TeacherApplicationRequest;
import com.team.updevic001.model.dtos.application.TeacherApplicationResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationFormMapper {

    public TeacherApplicationResponseDto toResponse(TeacherApplicationsEntity entity) {
        return new TeacherApplicationResponseDto(
                entity.getId(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getTeachingField(),
                entity.getLinkedinProfile(),
                entity.getGithubProfile(),
                entity.getPortfolio(),
                entity.getAdditionalInfo(),
                entity.getPhoneNumber(),
                entity.getCreatedAt(),
                entity.getStatus(),
                entity.getResultMessage(),
                entity.getCompletedAt()
        );
    }

    public TeacherApplicationsEntity toEntity(TeacherApplicationRequest request) {
        return TeacherApplicationsEntity.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .teachingField(request.getTeachingField())
                .linkedinProfile(request.getLinkedinProfile())
                .githubProfile(request.getGithubProfile())
                .portfolio(request.getPortfolio())
                .additionalInfo(request.getAdditionalInfo())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

}
