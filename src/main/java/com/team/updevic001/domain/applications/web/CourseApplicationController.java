package com.team.updevic001.domain.applications.web;

import com.team.updevic001.domain.applications.dto.ApplicationSearchDto;
import com.team.updevic001.domain.applications.dto.CourseApplicationRequest;
import com.team.updevic001.domain.applications.dto.CourseApplicationResponseDto;
import com.team.updevic001.domain.applications.dto.MessageDto;
import com.team.updevic001.domain.applications.excel.ExcelGenerator;
import com.team.updevic001.domain.applications.service.CourseApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class CourseApplicationController {

    private final CourseApplicationService courseApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseApplicationResponseDto createApplication(@Validated @RequestBody CourseApplicationRequest dto) {
        return courseApplicationService.createApplication(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CourseApplicationResponseDto getApplyById(@PathVariable UUID id) {
        return courseApplicationService.getApplication(id);
    }

    @PutMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    public void readApplication(@PathVariable UUID id) {
        courseApplicationService.readApplication(id);
    }

    @PutMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.OK)
    public void completeApplication(@PathVariable UUID id, @RequestBody MessageDto message) {
        courseApplicationService.completeApplication(id, message);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<CourseApplicationResponseDto> search(@RequestParam ApplicationSearchDto searchDto, Pageable pageable) {
        return courseApplicationService.search(searchDto, pageable);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public Page<CourseApplicationResponseDto> listApply(Pageable pageable) {
        return courseApplicationService.listApplications(pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApplication(@PathVariable UUID id) {
        courseApplicationService.deleteApplication(id);
    }

    @SneakyThrows
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportToExcel(@RequestParam ApplicationSearchDto searchDto) {

        List<CourseApplicationResponseDto> data = courseApplicationService.search(searchDto, Pageable.unpaged()).getContent();

        ByteArrayOutputStream excelOutputStream = ExcelGenerator.generateExcel(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=course_applications.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelOutputStream.toByteArray());
    }

}
