package com.team.updevic001.model.dtos.request;

import com.team.updevic001.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleDto {

    private Role role;

}
