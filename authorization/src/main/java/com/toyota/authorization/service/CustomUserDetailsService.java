package com.toyota.authorization.service;

import com.toyota.authorization.entity.Role;
import com.toyota.authorization.entity.User;
import com.toyota.authorization.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return new org.springframework.security.core.userdetails
                .User(user.getUsername(),user.getPassword(), mapRoleToAuthorities(user.getRoles()));
    }

    private Collection<GrantedAuthority> mapRoleToAuthorities(List<Role> roleList){
        return roleList.stream().map(role -> new SimpleGrantedAuthority(role.getRolename())).collect(Collectors.toList());
    }
}
