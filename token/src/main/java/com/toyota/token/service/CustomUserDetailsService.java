package com.toyota.token.service;

import com.toyota.token.entity.Role;
import com.toyota.token.entity.User;
import com.toyota.token.repository.UserRepository;
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

    /**
     * Loads a user's details by their username.
     *
     * @param username The username of the user whose details are to be loaded.
     * @return UserDetails containing the user's information.
     * @throws UsernameNotFoundException if the username is not found in the repository.
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return new org.springframework.security.core.userdetails
                .User(user.getUsername(),user.getPassword(), mapRoleToAuthorities(user.getRoles()));
    }

    /**
     * Maps a list of Role objects to a collection of GrantedAuthority.
     *
     * @param roleList The list of roles to be mapped.
     * @return A collection of GrantedAuthority based on the provided roles.
     */
    private Collection<GrantedAuthority> mapRoleToAuthorities(List<Role> roleList){
        return roleList.stream().map(role -> new SimpleGrantedAuthority(role.getRolename())).collect(Collectors.toList());
    }
}
