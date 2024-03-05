package com.toyota.usermanagement.service;

import com.toyota.usermanagement.dto.UserDto;
import com.toyota.usermanagement.entity.Role;
import com.toyota.usermanagement.entity.User;
import com.toyota.usermanagement.exception.NotFoundException;
import com.toyota.usermanagement.repository.RoleRepository;
import com.toyota.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void testSaveUser_whenUsernameIsNotTakenBefore_shouldSaveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        Role role = new Role();
        role.setRolename("testRole");

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByRolename("testRole")).thenReturn(Optional.of(role));

        String result = userService.saveUser(user, "testRole");

        assertEquals("User added successfully", result);

        verify(userRepository).existsByUsername(user.getUsername());
        verify(passwordEncoder).encode(user.getPassword());
        verify(roleRepository).findByRolename("testRole");
    }

    @Test
    public void testSaveUser_whenRoleNotFound_shouldThrowRoleNotFoundException() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(roleRepository.findByRolename("testRole")).thenThrow(new NotFoundException("Role not found: " +
                user.getUsername()));

        assertThrows(NotFoundException.class,
                () -> userService.saveUser(user, "testRole"));

        verify(userRepository).existsByUsername(user.getUsername());
        verify(roleRepository).findByRolename("testRole");

    }

    @Test
    public void testSaveUser_whenUsernameIsAlreadyExist_shouldReturnMessage() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        String result = userService.saveUser(user, "testRole");

        assertEquals("Username is already taken! Please choose another one." , result);

        verify(userRepository).existsByUsername(user.getUsername());
    }

    @Test
    void shouldReturnAllUsersWithUserDto_whenUserExist() {
        User user1 = new User();
        user1.setUsername("testUser1");
        user1.setPassword("testPassword1");
        user1.setRoles(List.of());

        User user2 = new User();
        user2.setUsername("testUser2");
        user2.setPassword("testPassword2");
        user2.setRoles(List.of());

        UserDto userDto1 = new UserDto("0", "testUser1", "testPassword1", List.of());
        UserDto userDto2 = new UserDto("1", "testUser2", "testPassword2", List.of());

        List<UserDto> expectedResult = Arrays.asList(userDto1, userDto2);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(UserDto.convert(user1)).thenReturn(userDto1);
        when(UserDto.convert(user2)).thenReturn(userDto2);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(result, expectedResult);

        Mockito.verify(userRepository).findAll();
    }

    @Test
    void testGetUserById_whenUserIdExists_returnUserWithUserDto() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById("testId");

        assertEquals("testUser", result.username());
    }

    @Test
    void testUpdateUser_whenUserExists_shouldReturnUserWithUserDto() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setRoles(List.of());

        UserDto userDto = new UserDto("0",
                "newTestUser",
                "newTestPassword",
                List.of());


        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.updateUser(userDto, "testId");

        assertEquals("newTestUser", result.username());
        assertEquals("encodedPassword", result.password());
    }

    @Test
    void testUpdateUser_whenUserIsNotExist_shouldReturnUserWithUserDto(){
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setRoles(List.of());

        UserDto userDto = new UserDto("0",
                "newTestUser",
                "newTestPassword",
                List.of());

        when(userRepository.findById(any())).thenThrow(new NotFoundException("User not found."));

        assertThrows(NotFoundException.class,
                () -> userService.updateUser(userDto, any()));

        verify(userRepository).findById(any());

    }

    @Test
    void testDeleteUser_whenUserIdExist_shouldDeleteUser() {
        String userId = "0";

        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        String result = userService.deleteUser(userId);

        assertEquals("User has deleted with id: " + userId, result);
    }

    @Test
    void testDeleteUser_whenUserIsNotExist_shouldReturnMessage() {
        String userId = "0";

        when(userRepository.existsById(userId)).thenReturn(false);

        String result = userService.deleteUser(userId);

        assertEquals("User not found with id: " + userId, result);
    }




    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}