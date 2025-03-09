package com.falcon.falcon.service.implementations;

import com.falcon.falcon.model.User;
import com.falcon.falcon.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service // this bean is used by the authentication provider to load the user by username and compare credentials
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found: " + username)); // we get the user by Username
        Collection<GrantedAuthority> grantedAuthorities = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet()); // we extract roles
        UserDetails userDetails = org.springframework.security.core.userdetails.User // pattern Builder : we build the User Details Object and return it
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(grantedAuthorities)
                .build();
        return userDetails;
    }
}
