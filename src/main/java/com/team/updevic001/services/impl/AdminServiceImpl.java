package com.team.updevic001.services.impl;

import com.team.updevic001.dao.entities.User;
import com.team.updevic001.dao.entities.UserRole;
import com.team.updevic001.dao.repositories.TeacherApplicationsRepository;
import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.dao.repositories.UserRoleRepository;
import com.team.updevic001.exceptions.ForbiddenException;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.response.admin_dasboard.DashboardResponse;
import com.team.updevic001.model.dtos.response.user.UserResponseForAdmin;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.model.enums.Status;
import com.team.updevic001.model.mappers.UserMapper;
import com.team.updevic001.services.interfaces.AdminService;
import com.team.updevic001.services.interfaces.AuthService;
import com.team.updevic001.services.interfaces.UserService;
import com.team.updevic001.specification.UserCriteria;
import com.team.updevic001.specification.UserSpecification;
import com.team.updevic001.utility.AuthHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.team.updevic001.model.enums.ExceptionConstants.FORBIDDEN_EXCEPTION;
import static com.team.updevic001.model.enums.ExceptionConstants.ROLE_NOT_FOUND;
import static com.team.updevic001.model.enums.ExceptionConstants.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserRoleRepository userRoleRepository;
    private final UserService userServiceImpl;
    private final AuthHelper authHelper;
    private final AuthService authService;
    private final TeacherApplicationsRepository teacherApplicationsRepository;

    @Override
    public void assignTeacherProfile(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(), USER_NOT_FOUND.getMessage().formatted(email)));

        var userRole = authService.createOrFetchRole(Role.TEACHER);
        if (!user.getRoles().contains(userRole)) {
            user.getRoles().add(userRole);
            userRepository.save(user);
        }
    }

    @Override
    public CustomPage<UserResponseForAdmin> getAllUsers(UserCriteria userCriteria, CustomPageRequest request) {
        int page = (request != null && request.getPage() >= 0) ? request.getPage() : 0;
        int size = (request != null && request.getSize() > 0) ? request.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size);

        Specification<User> filter = null;
        if (userCriteria.getFirstName() != null ||
                userCriteria.getEmail() != null ||
                userCriteria.getStatus() != null ||
                (userCriteria.getRoles() != null && !userCriteria.getRoles().isEmpty())) {
            filter = UserSpecification.filter(userCriteria);
        }
        var allUsers = (filter == null)
                ? userRepository.findAll(pageable)
                : userRepository.findAll(filter, pageable);

        return new CustomPage<>(
                userMapper.toResponseForAdmin(allUsers.getContent()),
                allUsers.getNumber(),
                allUsers.getSize());
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void activateUser(Long id) {
        var authenticatedUser = authHelper.getAuthenticatedUser();
        checkAdminRole(authenticatedUser, Role.ADMIN);
        userRepository.updateUserStatus(id, Status.ACTIVE);

    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deactivateUser(Long id) {
        var authenticatedUser = authHelper.getAuthenticatedUser();
        checkAdminRole(authenticatedUser, Role.ADMIN);
        userRepository.updateUserStatus(id, Status.INACTIVE);

    }

    @Override
    @CacheEvict(value = {"usersByRole", "users"}, allEntries = true)
    public void assignRoleToUser(Long id, Role role) {
        var authenticatedUser = authHelper.getAuthenticatedUser();
        var user = userServiceImpl.fetchUserById(id);
        checkAdminRole(authenticatedUser, Role.ADMIN);
        var userRole = userRoleRepository.findByName(role).orElseGet(() -> {
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
        var authenticatedUser = authHelper.getAuthenticatedUser();
        var user = userServiceImpl.fetchUserById(userId);
        checkAdminRole(authenticatedUser, Role.ADMIN);
        var findRole = user.getRoles()
                .stream()
                .filter(userRole -> Objects.equals(userRole.getName(), role))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND.getCode(), "This user does not have such a role."));
        user.getRoles().remove(findRole);
        userRepository.save(user);
    }

    public DashboardResponse getDashboard() {
        Object[] userCounts = userRepository.countUserStats();
        Long pendingApplications = teacherApplicationsRepository.countPendingApplications();
        DashboardResponse response = new DashboardResponse();
        response.setTotalUsers(((Number) userCounts[0]).longValue());
        response.setActiveUsers(((Number) userCounts[1]).longValue());
        response.setPendingUsers(((Number) userCounts[2]).longValue());
        response.setPendingApplicationsForTeaching(pendingApplications);
        return response;
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
            throw new ForbiddenException(FORBIDDEN_EXCEPTION.getCode(), FORBIDDEN_EXCEPTION.getMessage());
        }
    }
}
