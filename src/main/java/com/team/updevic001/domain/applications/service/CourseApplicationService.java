package com.team.updevic001.domain.applications.service;

import com.team.updevic001.domain.applications.dto.ApplicationSearchDto;
import com.team.updevic001.domain.applications.dto.CourseApplicationRequest;
import com.team.updevic001.domain.applications.dto.CourseApplicationResponseDto;
import com.team.updevic001.domain.applications.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseApplicationService {

    CourseApplicationResponseDto createApplication(CourseApplicationRequest dto);

    CourseApplicationResponseDto getApplication(UUID id);

    Page<CourseApplicationResponseDto> listApplications(Pageable pageable);

    void deleteApplication(UUID id);

    Page<CourseApplicationResponseDto> search(ApplicationSearchDto searchDto, Pageable pageable);

    void readApplication(UUID id);

    void completeApplication(UUID id, MessageDto message);
}
