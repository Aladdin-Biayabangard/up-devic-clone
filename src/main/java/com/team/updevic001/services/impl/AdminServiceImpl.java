package com.team.updevic001.services.impl;

import com.team.updevic001.configuration.mappers.UserMapper;
import com.team.updevic001.dao.entities.Teacher;
import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserRole;
import com.team.updevic001.dao.repositories.TeacherRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.dao.repositories.UserRoleRepository;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.exceptions.ResourceNotFoundException;
import com.team.updevic001.model.dtos.response.user.ResponseUserDto;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.enums.Status;
import com.team.updevic001.model.projection.UserView;
import com.team.updevic001.services.interfaces.AdminService;
import com.team.updevic001.services.interfaces.UserService;
import com.team.updevic001.utility.AuthHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserRoleRepository userRoleRepository;
    private final TeacherRepository teacherRepository;
    private final UserService userServiceImpl;
    private final AuthHelper authHelper;

    @Override
    public void assignTeacherProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Enter the email you registered with."));
        UserRole userRole = userRoleRepository.findByName(Role.TEACHER).orElseGet(() -> {
            UserRole role = UserRole.builder()
                    .name(Role.TEACHER)
                    .build();
            return userRoleRepository.save(role);

        });
        if (!user.getRoles().contains(userRole)) {
            user.getRoles().add(userRole);
            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacherRepository.save(teacher);
            userRepository.save(user);
        }
    }


    @Override
    @Cacheable(value = "users", key = "'after:' + #afterId + ':limit:' + #limit")
    public List<ResponseUserDto> getAllUsers(Long afterId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<UserView> users = userRepository.findByIdGreaterThanOrderByIdAsc(afterId, pageable);
        System.out.println(users);
        if (users.isEmpty()) {
            log.info("No user found!");
        } else {
            log.info("Found {} users.", users.size());
        }
        return users.stream().map(userMapper::toResponseFromView).toList();
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void activateUser(Long id) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        checkAdminRole(authenticatedUser, Role.ADMIN);
        userRepository.updateUserStatus(id, Status.ACTIVE);

    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deactivateUser(Long id) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        checkAdminRole(authenticatedUser, Role.ADMIN);
        userRepository.updateUserStatus(id, Status.INACTIVE);

    }

    @Override
    @CacheEvict(value = {"usersByRole", "users"}, allEntries = true)
    public void assignRoleToUser(Long id, Role role) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        User user = userServiceImpl.fetchUserById(id);
        checkAdminRole(authenticatedUser, Role.ADMIN);
        UserRole userRole = userRoleRepository.findByName(role).orElseGet(() -> {
            UserRole newRole = UserRole.builder()
                    .name(role)
                    .build();
            return userRoleRepository.save(newRole);
        });
        if (!user.getRoles().contains(userRole)) {
            saveUserRole(userRole);
            user.getRoles().add(userRole);
            saveUser(user);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"usersByRole", "users"}, allEntries = true)
    public void removeRoleFromUser(Long userId, Role role) {
        User authenticatedUser = authHelper.getAuthenticatedUser();
        User user = userServiceImpl.fetchUserById(userId);
        checkAdminRole(authenticatedUser, Role.ADMIN);
        UserRole findRole = user.getRoles()
                .stream()
                .filter(userRole -> Objects.equals(userRole.getName(), role))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("This user does not have such a role."));

        user.getRoles().remove(findRole);
        userRepository.save(user);
    }

    @Override
    @Cacheable(value = "usersByRole", key = "#role.name()")
    public List<ResponseUserDto> getUsersByRole(Role role) {
        List<UserView> users = userRepository.findUsersByRole(role);
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        } else {
            log.info("Found {} users with role: {}", users.size(), role);
        }
        return users.stream().map(userMapper::toResponseFromView).toList();
    }


    @Override
    @CacheEvict(value = {"users", "usersByRole", "userCount"}, allEntries = true)
    public void deleteUsers() {
        userRepository.deleteAll();
    }

    @Override
    @Cacheable("userCount")
    public Long countUsers() {
        return (long) userRepository.countNonAdminUsers();
    }

    private void saveUser(User user) {
        userRepository.save(user);
    }

    private void saveUserRole(UserRole userRole) {
        userRoleRepository.save(userRole);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void softyDeleteUser(Long userId) {
        userRepository.updateUserStatus(userId, Status.DELETED);
    }

    public void checkAdminRole(User user, Role role) {
        if (!userRoleRepository.existsByUserAndRole(user, role)) {
            throw new ForbiddenException("No permission");
        }
    }
}
