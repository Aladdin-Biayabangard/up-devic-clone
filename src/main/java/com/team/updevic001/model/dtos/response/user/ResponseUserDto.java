package com.team.updevic001.model.dtos.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserDto {

    private String firstName;
    private String lastName;
    private String email;

}
