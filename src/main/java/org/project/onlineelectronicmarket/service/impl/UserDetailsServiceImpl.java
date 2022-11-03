package org.project.onlineelectronicmarket.service.impl;

import org.project.onlineelectronicmarket.model.User;
import org.project.onlineelectronicmarket.model.util.CustomUserDetails;
import org.project.onlineelectronicmarket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class UserDetailsServiceImpl implements UserDetailsService {

        private final UserRepository userRepository;

        @Autowired
        public UserDetailsServiceImpl(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByName(username);

                UserDetails build = org.springframework.security.core.userdetails.User
                        .withUsername(user.getName())
                        .password(user.getPassword())
                        .roles(user.getRoles().stream().map(Enum::name).toArray(String[]::new)).build();
                return new CustomUserDetails(build, user);
        }
}