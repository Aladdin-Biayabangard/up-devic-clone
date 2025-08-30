package com.team.updevic001.model.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    String firstName;
    String lastName;
    List<String> role = new ArrayList<>();
    String accessToken;
    Long refreshToken;
}

