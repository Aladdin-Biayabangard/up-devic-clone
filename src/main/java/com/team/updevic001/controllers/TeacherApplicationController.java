package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.application.ApplicationSearchDto;
import com.team.updevic001.model.dtos.application.TeacherApplicationRequest;
import com.team.updevic001.model.dtos.application.TeacherApplicationResponseDto;
import com.team.updevic001.model.dtos.application.MessageDto;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.utility.ExcelGenerator;
import com.team.updevic001.services.interfaces.TeacherApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/applications")
@RequiredArgsConstructor
public class TeacherApplicationController {

    private final TeacherApplicationService teacherApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeacherApplicationResponseDto createApplication(@Validated @RequestBody TeacherApplicationRequest dto) {
        return teacherApplicationService.createApplication(dto);
    }

    @GetMapping("/{id}")
    public TeacherApplicationResponseDto getApplyById(@PathVariable UUID id) {
        return teacherApplicationService.getApplication(id);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    public void readApplication(@PathVariable UUID id) {
        teacherApplicationService.readApplication(id);
    }

    @PutMapping("/{id}/success")
    @ResponseStatus(HttpStatus.OK)
    public void approvedApplication(@PathVariable UUID id, @RequestBody MessageDto message) {
        teacherApplicationService.approvedApplication(id, message);
    }

    @PutMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.OK)
    public void rejectApplication(@PathVariable UUID id, @RequestBody MessageDto message) {
        teacherApplicationService.rejectApplication(id, message);
    }


    @GetMapping("/search")
    public CustomPage<TeacherApplicationResponseDto> listApplications(
            @ModelAttribute ApplicationSearchDto searchDto,
            @ModelAttribute CustomPageRequest pageRequest) {
        return teacherApplicationService.listApplications(searchDto, pageRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApplication(@PathVariable UUID id) {
        teacherApplicationService.deleteApplication(id);
    }

    @SneakyThrows
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel(@ModelAttribute ApplicationSearchDto searchDto) {

        List<TeacherApplicationResponseDto> data = teacherApplicationService.listApplications(searchDto, null).getContent();

        ByteArrayOutputStream excelOutputStream = ExcelGenerator.generateExcel(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=teacher_applications.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelOutputStream.toByteArray());
    }

}
