package com.team.updevic001.model.dtos.application;

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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherApplicationRequest {

    @NotBlank(message = "Adınızı qeyd edin")
    String fullName;

    @NotBlank
    @Email(message = "Zəhmət olmasa düzgün email ünvanı daxil edin")
    String email;

    @NotBlank(message = "Tədris etmək istədiyiniz sahəni qeyd edin")
    String teachingField;

    @NotBlank(message = "LinkedIn profil linkinizi daxil edin")
    String linkedinProfile;

    @NotBlank(message = "GitHub profil linkinizi daxil edin")
    String githubProfile;

    String portfolio;

    String additionalInfo;

    @NotNull
    @NotEmpty
    String phoneNumber;

}
