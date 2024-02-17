package com.toyota.usermanagement.service;

import com.toyota.usermanagement.dto.UserDto;
import com.toyota.usermanagement.entity.Role;
import com.toyota.usermanagement.entity.User;
import com.toyota.usermanagement.exception.NotFoundException;
import com.toyota.usermanagement.repository.RoleRepository;
import com.toyota.usermanagement.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService{
    private static Logger logger = LogManager.getLogger(UserService.class);
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
            logger.warn("Username must not be empty.");
            return "Username mustn't be empty!";
        }

        if(userRepository.existsByUsername(username)){
            logger.warn("Username is already taken.");
            return "Username is already taken! Please choose another one.";
        }

        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        Role roles = roleRepository.findByRolename(rolename)
                .orElseThrow(() -> new NotFoundException("Role not found: " + rolename));
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        logger.info("User added successfully");
        return "User added successfully";
    }

    public String saveRole(Role role) {
        roleRepository.save(role);

        logger.info("Role added successfully");
        return "Role added successfully";
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = new ArrayList<>();

        logger.info("Getting all users");
        return users.stream()
                .map(UserDto::convert)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("User not found."));

        logger.info("Getting user with id: " + userId);
        return UserDto.convert(user);

    }

    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("User not found."));

        user.setUsername(userDto.username());
        user.setPassword(encoder.encode(userDto.password()));

        User updatedUser = userRepository.save(user);

        logger.info("Updating user object");
        return UserDto.convert(updatedUser);
    }

    public String deleteUser(String userId) {
        if(userRepository.existsById(userId)){
            userRepository.deleteById(userId);
            logger.info("User has deleted with id: " + userId);
            return "User has deleted with id: " + userId;
        }
        else{
            logger.warn("User not found with id: " + userId);
            return "User not found with id: " + userId;
        }
    }

}
