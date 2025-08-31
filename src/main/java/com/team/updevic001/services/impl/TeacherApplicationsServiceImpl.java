package com.team.updevic001.services.impl;

import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.mail.EmailServiceImpl;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.enums.ApplicationStatus;
import com.team.updevic001.model.dtos.application.ApplicationSearchDto;
import com.team.updevic001.model.dtos.application.TeacherApplicationRequest;
import com.team.updevic001.model.dtos.application.TeacherApplicationResponseDto;
import com.team.updevic001.model.dtos.application.MessageDto;
import com.team.updevic001.model.mappers.ApplicationFormMapper;
import com.team.updevic001.services.interfaces.AdminService;
import com.team.updevic001.specification.CourseApplicationSpecification;
import com.team.updevic001.dao.entities.TeacherApplicationsEntity;
import com.team.updevic001.dao.repositories.TeacherApplicationsRepository;
import com.team.updevic001.services.interfaces.TeacherApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.team.updevic001.mail.EmailTemplate.APPLICATION_APPROVED;
import static com.team.updevic001.mail.EmailTemplate.APPLICATION_CANCELLED;
import static com.team.updevic001.mail.EmailTemplate.APPLICATION_FORM_INFO_ENG;
import static com.team.updevic001.model.enums.ExceptionConstants.APPLICATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TeacherApplicationsServiceImpl implements TeacherApplicationService {


    private final TeacherApplicationsRepository teacherApplicationsRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final ApplicationFormMapper applicationFormMapper;
    private final AdminService adminService;

    @Override
    public TeacherApplicationResponseDto createApplication(TeacherApplicationRequest dto) {
        var entity = applicationFormMapper.toEntity(dto);
        entity.setStatus(ApplicationStatus.NEW);
        teacherApplicationsRepository.save(entity);
        Map<String, String> placeholders = Map.of("userName", dto.getFullName());
        emailServiceImpl.sendEmail(dto.getEmail(), APPLICATION_FORM_INFO_ENG, placeholders);
        return applicationFormMapper.toResponse(entity);
    }

    @Override
    public TeacherApplicationResponseDto getApplication(UUID id) {
        var entity = findById(id);
        return applicationFormMapper.toResponse(entity);
    }

    @Override
    public CustomPage<TeacherApplicationResponseDto> listApplications(ApplicationSearchDto searchDto,
                                                                      CustomPageRequest pageRequest) {
        Pageable pageable;
        if (pageRequest != null) {
            pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize());
        } else {
            pageable = Pageable.unpaged();
        }

        boolean hasFilters = searchDto != null && (
                searchDto.getEmail() != null ||
                searchDto.getFullName() != null ||
                searchDto.getPhone() != null ||
                searchDto.getTeachingField() != null ||
                searchDto.getCreatedAtFrom() != null ||
                searchDto.getCreatedAtTo() != null ||
                searchDto.getStatus() != null
        );

        Page<TeacherApplicationsEntity> resultPage;

        if (hasFilters) {
            resultPage = teacherApplicationsRepository.findAll(
                    CourseApplicationSpecification.buildSpecification(searchDto),
                    pageable
            );
        } else {
            resultPage = teacherApplicationsRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        return new CustomPage<>(
                resultPage.getContent()
                        .stream()
                        .map(applicationFormMapper::toResponse)
                        .toList(),
                resultPage.getNumber(),
                resultPage.getSize()
        );
    }


    @Override
    public void deleteApplication(UUID id) {
        findById(id);
        teacherApplicationsRepository.deleteById(id);
    }

    @Override
    public void readApplication(UUID id) {
        var entity = findById(id);
        if (entity.getStatus() == null || entity.getStatus() == ApplicationStatus.NEW) {
            entity.setStatus(ApplicationStatus.READ);
            teacherApplicationsRepository.save(entity);
        }
    }

    @Override
    @Transactional
    public void approvedApplication(UUID id, MessageDto message) {
        var entity = findById(id);
        entity.setStatus(ApplicationStatus.APPROVED);
        entity.setResultMessage(message.getMessage());
        entity.setCompletedAt(LocalDateTime.now());
        teacherApplicationsRepository.save(entity);
        adminService.assignTeacherProfile(entity.getEmail());
        Map<String, String> placeholders = Map.of("userName", entity.getFullName());
        emailServiceImpl.sendEmail(entity.getEmail(), APPLICATION_APPROVED, placeholders);
    }

    @Override
    @Transactional
    public void rejectApplication(UUID id, MessageDto message) {
        var entity = findById(id);
        entity.setStatus(ApplicationStatus.REJECTED);
        entity.setResultMessage(message.getMessage());
        entity.setCompletedAt(LocalDateTime.now());
        teacherApplicationsRepository.save(entity);
        Map<String, String> placeholders = Map.of("userName", entity.getFullName(), "cancellationReason", message.getMessage());
        emailServiceImpl.sendEmail(entity.getEmail(), APPLICATION_CANCELLED, placeholders);
    }


    private TeacherApplicationsEntity findById(UUID id) {
        return teacherApplicationsRepository.findById(id).orElseThrow(() -> new NotFoundException(APPLICATION_NOT_FOUND.getCode(),
                APPLICATION_NOT_FOUND.getMessage().formatted(id)));
    }

}
