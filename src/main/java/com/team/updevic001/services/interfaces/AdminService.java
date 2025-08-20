
package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.dtos.response.user.UserResponseForAdmin;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.projection.UserView;
import com.team.updevic001.specification.UserCriteria;

import java.util.List;

public interface AdminService {

    void assignTeacherProfile(String email);

    CustomPage<UserResponseForAdmin> getAllUsers(UserCriteria userCriteria, CustomPageRequest pageRequest);

    void assignRoleToUser(Long userId, Role role);

    void removeRoleFromUser(Long userId, Role role);


    void activateUser(Long id);

    void softyDeleteUser(Long userId);

    void deactivateUser(Long id);

    Long countUsers();

    void deleteUsers();


}
