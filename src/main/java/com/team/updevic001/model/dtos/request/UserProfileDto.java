package com.team.updevic001.model.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
//
//    private String firstName;
//    private String lastName;
//    private String password;

    private String bio;

    private String socialLink;

    private String skill;
}
