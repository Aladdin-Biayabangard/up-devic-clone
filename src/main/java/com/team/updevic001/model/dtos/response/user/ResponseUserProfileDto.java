package com.team.updevic001.model.dtos.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseUserProfileDto {

    private String firstName;

    private String lastName;

    private String profilePhoto_url;

    private String bio;

    private Set<String> socialLinks;

    private Set<String> skills;

    private List<String> roles;
}
