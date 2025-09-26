package com.team.updevic001.utility;

import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.model.enums.RecipientsGroup;
import com.team.updevic001.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecipientResolver {

    private final UserRepository userRepository;

    public Set<String> resolveRecipients(RecipientsGroup group) {
        return switch (group) {
            case ALL_USERS -> userRepository.findAllUserEmail();
            case TEACHERS -> userRepository.findEmailsByRole(Role.TEACHER);
            case STUDENTS -> userRepository.findEmailsByRole(Role.STUDENT);
            case ADMINISTRATORS -> userRepository.findEmailsByRole(Role.ADMIN);
            case INACTIVE_USERS -> userRepository.findEmailsInactiveUsers();
            case INDIVIDUAL -> Set.of();
        };
    }
}
