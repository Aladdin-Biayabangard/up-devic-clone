package com.team.updevic001.domain.applications.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseApplicationRequest {

    @NotNull
    UUID courseId;

    @NotBlank
    String courseName;

    @NotNull
    @NotEmpty
    @Email(message = "Invalid email address")
    String email;

    @NotNull
    @NotEmpty
    String fullName;

    @NotNull
    @NotEmpty
    String phone;

}
