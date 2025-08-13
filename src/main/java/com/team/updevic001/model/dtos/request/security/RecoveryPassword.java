package com.team.updevic001.model.dtos.request.security;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RecoveryPassword {
    @NotNull(message = "new password can not be null")
    @Size(min = 6, max = 30)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d!@#$%^&*]{6,}$",
            message = "Invalid Password: Must be at least 6 characters long, with at least one uppercase letter, one lowercase letter, and one digit. Special characters (!@#$%^&*) are allowed but not required."
    )

    private String newPassword;

    @NotNull(message = "retry password can not be null")
    private String retryPassword;
}