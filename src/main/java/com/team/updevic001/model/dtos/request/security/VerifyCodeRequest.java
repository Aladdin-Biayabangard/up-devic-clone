package com.team.updevic001.model.dtos.request.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerifyCodeRequest {
    private String email;
    private String code;
}