package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.application.ApplicationSearchDto;
import com.team.updevic001.model.dtos.application.TeacherApplicationRequest;
import com.team.updevic001.model.dtos.application.TeacherApplicationResponseDto;
import com.team.updevic001.model.dtos.application.MessageDto;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import java.util.UUID;

public interface TeacherApplicationService {

    TeacherApplicationResponseDto createApplication(TeacherApplicationRequest dto);

    TeacherApplicationResponseDto getApplication(UUID id);

    CustomPage<TeacherApplicationResponseDto> listApplications(ApplicationSearchDto searchDto,
                                                               CustomPageRequest pageRequest);

    void deleteApplication(UUID id);


    void readApplication(UUID id);

    void approvedApplication(UUID id, MessageDto message);

    void rejectApplication(UUID id, MessageDto message);
}
