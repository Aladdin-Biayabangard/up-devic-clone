
package com.team.updevic001.services.interfaces;

import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.projection.UserView;

import java.util.List;

public interface AdminService {

    void assignTeacherProfile(String email);

    List<ResponseUserDto> getAllUsers(Long afterId, int limit);

    void assignRoleToUser(Long userId, Role role);

    void removeRoleFromUser(Long userId, Role role);

    List<ResponseUserDto> getUsersByRole(Role role);

    void activateUser(Long id);

    void softyDeleteUser(Long userId);

    void deactivateUser(Long id);

    Long countUsers();

    void deleteUsers();


}
