package com.team.updevic001.controllers;

import com.team.updevic001.model.dtos.page.CustomPage;
import com.team.updevic001.model.dtos.page.CustomPageRequest;
import com.team.updevic001.model.dtos.response.user.UserResponseForAdmin;
import com.team.updevic001.model.enums.Role;
import com.team.updevic001.services.interfaces.AdminService;
import com.team.updevic001.specification.UserCriteria;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminServiceImpl;


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


    @Operation(summary = "Shows the number of users")
    @GetMapping(path = "/users/count")
    public Long getUsersCount() {
        return adminServiceImpl.countUsers();
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

    @Operation(summary = "All User Deletes.")
    @DeleteMapping(path = "/all")
    @ResponseStatus(NO_CONTENT)
    public void deleteUsers() {
        adminServiceImpl.deleteUsers();
    }

//    @Operation(summary = "Bütün kursları silmək üçün.")
//    @DeleteMapping(path = "delete-courses")
//    @ResponseStatus(NO_CONTENT)
//    public void deleteAllCourses() {
//        List<Course> all = courseRepository.findAll();
//        all.forEach(course -> {
//            List<User> users = userRepository.findAllByWishlistContaining(course);
//            if (!users.isEmpty()) {
//                users.forEach(user -> user.getWishlist().remove(course));
//                userRepository.saveAll(users);
//            }
//        });
//        courseRepository.deleteAll();
//    }

}
