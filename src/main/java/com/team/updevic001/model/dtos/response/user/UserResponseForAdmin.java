package com.team.updevic001.model.dtos.response.user;

import com.team.updevic001.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseForAdmin {
    private String firstName;

    private String lastName;

    private String email;

    private List<String> roles;

    private Status status;
}
