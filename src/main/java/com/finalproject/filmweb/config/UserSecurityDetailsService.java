package com.finalproject.filmweb.config;

import com.finalproject.filmweb.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class UserSecurityDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByUserName(login)
                .filter(userEntity -> userEntity.getEnabled())
                .orElseThrow(() -> {
                    log.error("User not found of user not performed token!");
                    return new UsernameNotFoundException("User not found: " + login);
                });
    }
}

