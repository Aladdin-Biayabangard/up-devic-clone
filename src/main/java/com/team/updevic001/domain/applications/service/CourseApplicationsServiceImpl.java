package com.team.updevic001.domain.applications.service;


import com.team.updevic001.domain.applications.domain.ApplicationStatus;
import com.team.updevic001.domain.applications.dto.ApplicationSearchDto;
import com.team.updevic001.domain.applications.dto.CourseApplicationRequest;
import com.team.updevic001.domain.applications.dto.CourseApplicationResponseDto;
import com.team.updevic001.domain.applications.dto.EmailDto;
import com.team.updevic001.domain.applications.dto.MessageDto;
import com.team.updevic001.domain.applications.repo.CourseApplicationSpecification;
import com.team.updevic001.domain.applications.repo.CourseApplicationsEntity;
import com.team.updevic001.domain.applications.repo.CourseApplicationsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CourseApplicationsServiceImpl implements CourseApplicationService {

    private static final String COURSE_NAME = "courseName";
    private static final String FULL_NAME = "fullName";

    private final EmailConfigurationProperties emailConfigurationProperties;
    private final CourseApplicationsRepository courseApplicationsRepository;
    private final ModelMapper mapper;

    @Override
    public CourseApplicationResponseDto createApplication(CourseApplicationRequest dto) {
        CourseApplicationsEntity application = mapper.map(dto, CourseApplicationsEntity.class);
        application.setId(UUID.randomUUID());
        application.setCreatedAt(LocalDateTime.now());
        application.setStatus(ApplicationStatus.NEW);
        courseApplicationsRepository.save(application);
        return mapper.map(application, CourseApplicationResponseDto.class);
    }

    @Override
    public CourseApplicationResponseDto getApplication(UUID id) {
        CourseApplicationsEntity courseApplicationsEntity = findById(id);
        return mapper.map(courseApplicationsEntity, CourseApplicationResponseDto.class);
    }

    @Override
    public Page<CourseApplicationResponseDto> listApplications(Pageable pageable) {
        return courseApplicationsRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(application -> mapper.map(application, CourseApplicationResponseDto.class));
    }

    @Override
    public void deleteApplication(UUID id) {
        findById(id);
        courseApplicationsRepository.deleteById(id);
    }

    @Override
    public Page<CourseApplicationResponseDto> search(ApplicationSearchDto searchDto,
                                                     Pageable pageable) {
        return courseApplicationsRepository.findAll(
                CourseApplicationSpecification.buildSpecification(searchDto),
                pageable).map(entity -> mapper.map(entity, CourseApplicationResponseDto.class));
    }

    @Override
    public void readApplication(UUID id) {
        final CourseApplicationsEntity entity = courseApplicationsRepository.findById(id).orElseThrow();
        if (entity.getStatus() == null || entity.getStatus() == ApplicationStatus.NEW) {
            entity.setStatus(ApplicationStatus.READ);
            courseApplicationsRepository.save(entity);
        }
    }

    @Override
    public void completeApplication(UUID id, MessageDto message) {
        final CourseApplicationsEntity entity = courseApplicationsRepository.findById(id)
                .orElseThrow();
        entity.setStatus(ApplicationStatus.COMPLETED);
        entity.setResultMessage(message.getMessage());
        entity.setCompletedAt(LocalDateTime.now());
        courseApplicationsRepository.save(entity);
    }

    private EmailDto createEmail(CourseApplicationRequest dto) {
        return EmailDto
                .builder()
                .from(emailConfigurationProperties.getSender())
                .to(dto.getEmail())
                .subject(emailConfigurationProperties.getSubject())
                .variables(Map.of(FULL_NAME, dto.getFullName(), COURSE_NAME, dto.getCourseName()))
                .template(EmailTemplate.COURSE_ENROLLMENT)
                .build();
    }

    private CourseApplicationsEntity findById(UUID id) {
        return courseApplicationsRepository.findById(id).orElseThrow();
    }

}
