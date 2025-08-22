package com.team.updevic001.configuration.config.security;

import com.team.updevic001.dao.repositories.UserRepository;
import com.team.updevic001.exceptions.NotFoundException;
import com.team.updevic001.model.enums.ExceptionConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.team.updevic001.model.enums.ExceptionConstants.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.getCode(),
                USER_NOT_FOUND.getMessage().formatted(email)));
    }
}
