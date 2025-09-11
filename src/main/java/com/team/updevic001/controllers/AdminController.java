package com.team.updevic001.controllers;

import com.team.updevic001.criteria.CertificateCriteria;
import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.response.admin_dasboard.CertificateResponseForAdmin;
import com.team.updevic001.model.dtos.response.admin_dasboard.DashboardResponse;
import com.team.updevic001.model.dtos.response.user.UserResponseForAdmin;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.services.impl.CertificateService;
import com.team.updevic001.services.interfaces.AdminService;
import com.team.updevic001.specification.UserCriteria;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminServiceImpl;
    private final CertificateService certificateService;

    @Operation(summary = "Adding a teacher profile to a student")
    @PostMapping(path = "/assign/{email}")
    @ResponseStatus(CREATED)
    public void assignTeacherProfile(@PathVariable String email) {
        adminServiceImpl.assignTeacherProfile(email);
    }

    @Operation(summary = "Activates the user")
    @PutMapping("/users/{id}/activate")
    public void activateUser(@PathVariable Long id) {
        adminServiceImpl.activateUser(id);
    }

    @Operation(summary = "Deactivates the user")
    @PutMapping("/users/{id}/deactivate")
    public void deactivateUser(@PathVariable Long id) {
        adminServiceImpl.deactivateUser(id);
    }

    @Operation(summary = "Adds a role to a user")
    @PutMapping("/users/{id}/assign/role")
    public void assignRoleToUser(@PathVariable Long id,
                                 @RequestParam Role role) {
        adminServiceImpl.assignRoleToUser(id, role);
    }

    @Operation(summary = "Shows all users")
    @GetMapping("/search")
    public CustomPage<UserResponseForAdmin> getAllUsers(UserCriteria userCriteria, CustomPageRequest pageRequest) {
        return adminServiceImpl.getAllUsers(userCriteria, pageRequest);
    }

    @GetMapping(path = "/certificates")
    public CustomPage<CertificateResponseForAdmin> getAllCertificates(@RequestParam CertificateCriteria criteria,
                                                                      @RequestParam CustomPageRequest request) {

        return certificateService.getAllCertificates(criteria, request);
    }

    @Operation(summary = "Shows the number of users")
    @GetMapping(path = "/dashboard")
    public DashboardResponse getDashboard() {
        return adminServiceImpl.getDashboard();
    }

    @Operation(summary = "Removes a user's role!")
    @PutMapping(path = "/users/{id}/role")
    @ResponseStatus(NO_CONTENT)
    public void removeRoleFromUser(@PathVariable Long id,
                                   @RequestParam Role role) {
        adminServiceImpl.removeRoleFromUser(id, role);
    }

    @DeleteMapping(path = "/users/{id}")
    @ResponseStatus(NO_CONTENT)
    public void softyDeleteUser(@PathVariable Long id) {
        adminServiceImpl.softyDeleteUser(id);
    }

}
