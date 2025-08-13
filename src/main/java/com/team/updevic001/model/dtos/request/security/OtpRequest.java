package com.team.updevic001.model.dtos.request.security;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpRequest {

    @NotBlank(message = "Email is required")
    String email;
    @NotBlank(message = "OTP code is required")
    Integer otpCode;
}
