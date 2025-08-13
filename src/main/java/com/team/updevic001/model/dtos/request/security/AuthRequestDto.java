package com.team.updevic001.model.dtos.request.security;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthRequestDto {
    @NotBlank(message = "Email is required")
    String email;
    @NotBlank(message = "Password is required")
    String password;
}
