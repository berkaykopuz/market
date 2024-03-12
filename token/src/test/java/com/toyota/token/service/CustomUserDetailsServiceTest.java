package com.toyota.token.service;

import com.toyota.token.entity.Role;
import com.toyota.token.entity.User;
import com.toyota.token.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService detailsService;
    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);

        detailsService = new CustomUserDetailsService(userRepository);
    }

    /*@Test
    void testLoadUserByUsername_whenUserIsAvailable_shouldReturnUserWithGrantedRoles() {
        Role role = generateRole();
        User user = generateUser(role);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails result = detailsService.loadUserByUsername(user.getUsername());

        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername(user.getUsername());

    }*/

    @Test
    private User generateUser(Role role){
        String username = "testUser";
        User user = new User();

        user.setRoles(Arrays.asList(role));

        return user;
    }

    private Role generateRole(){
        Role role = new Role();
        role.setRolename("ROLE_USER");

        return role;
    }

    private Collection<GrantedAuthority> mapRoleToAuthorities(List<Role> roleList){
        return roleList.stream().map(role -> new SimpleGrantedAuthority(role.getRolename()))
                .collect(Collectors.toList());
    }
}