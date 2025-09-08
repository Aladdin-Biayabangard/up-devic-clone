package com.team.updevic001.model.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

    private String bio;

    private String firstName;

    private String lastName;

    private Set<String> socialLink;

    private Set<String> skill;
}
