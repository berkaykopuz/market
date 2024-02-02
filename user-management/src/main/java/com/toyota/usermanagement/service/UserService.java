package com.toyota.usermanagement.service;

import com.toyota.usermanagement.dto.UserDto;
import com.toyota.usermanagement.entity.Role;
import com.toyota.usermanagement.entity.User;
import com.toyota.usermanagement.repository.RoleRepository;
import com.toyota.usermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    public String saveUser(User user, String rolename){
        String username = user.getUsername();

        if(username.isEmpty()){
            return "Username mustn't be empty!";
        }

        if(userRepository.existsByUsername(username)){
            return "Username is already taken! Please choose another one.";
        }

        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Role roles = roleRepository.findByRolename(rolename)
                .orElseThrow(() -> new RuntimeException("Role not found: " + rolename));
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return "User added successfully";
    }

    public String saveRole(Role role) {
        roleRepository.save(role);
        return "Role added successfully";
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        return users.stream()
                .map(UserDto::convert)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found."));

        return UserDto.convert(user);

    }

    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found."));

        user.setUsername(userDto.username());
        user.setPassword(encoder.encode(userDto.password()));

        User updatedUser = userRepository.save(user);
        return UserDto.convert(updatedUser);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

}
