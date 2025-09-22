package com.team.updevic001.model.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEmailInfo {

    private String firstName;
    private String lastName;
    private String email;
}
