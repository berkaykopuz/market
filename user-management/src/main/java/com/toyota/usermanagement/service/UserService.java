package com.toyota.usermanagement.service;

import com.toyota.usermanagement.dto.UserDto;
import com.toyota.usermanagement.dto.UserResponse;
import com.toyota.usermanagement.entity.Role;
import com.toyota.usermanagement.entity.User;
import com.toyota.usermanagement.exception.BadRequestException;
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

    /**
     * Saves a new user to the repository with the specified role.
     *
     * @param user The User object to be saved.
     * @param rolename The name of the role to be assigned to the user.
     * @return A string message indicating the result of the operation.
     * @throws NotFoundException if the role name does not exist in the repository.
     */
    public String saveUser(User user, String rolename){
        logger.trace("Entering saveUser method");
        String username = user.getUsername();

        if(username.isEmpty()){
            logger.error("Username must not be empty.");
            throw new BadRequestException("Username must not be empty");
        }

        if(userRepository.existsByUsername(username)){
            logger.error("Username is already taken");
            throw new BadRequestException("Username is already taken");
        }

        logger.trace("Encoding password");
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        logger.trace("Finding role by rolename");
        Role roles = roleRepository.findByRolename(rolename)
                .orElseThrow(() -> new NotFoundException("Role not found: " + rolename));
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        logger.info("User added successfully");
        return "User added successfully";
    }

    /**
     * Saves a new role to the repository.
     *
     * @param role The Role object to be saved.
     * @return A string message indicating the result of the operation.
     */
    public String saveRole(Role role) {
        if(role.getRolename().isEmpty()){
            logger.error("Given role name is empty");
            throw new BadRequestException("Role must not be empty");
        }
        if(roleRepository.existsByRolename(role.getRolename())){
            logger.error("Rolename is already taken");
            throw new BadRequestException("Rolename is already taken");
        }

        roleRepository.save(role);

        logger.info("Role added successfully");
        return "Role added successfully";
    }

    /**
     * Retrieves all users from the repository and returns them as a list of UserDto.
     *
     * @return A list of UserDto representing all users.
     */
    public List<UserResponse> getAllUsers() {
        logger.debug("Attempting to getting all users");
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::convert)
                .collect(Collectors.toList());

        logger.info("Getting all users");

        return userResponses;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The UserDto representation of the retrieved user.
     * @throws NotFoundException if the user with the given ID is not found.
     */
    public UserResponse getUserById(String userId) {
        logger.debug("Attempting to getting user with id: " + userId);
        User user = userRepository.findById(userId).orElseThrow(()-> new NotFoundException("User not found"));

        logger.info("Getting user with id: " + userId);
        return UserResponse.convert(user);

    }

    /**
     * Updates a user's information based on the provided UserDto and ID.
     *
     * @param userDto The UserDto containing the updated user information.
     * @param userId The ID of the user to update.
     * @return The UserDto representation of the updated user.
     * @throws NotFoundException if the user with the given ID is not found.
     */
    public UserResponse updateUser(UserDto userDto, String userId) {
        logger.debug("Attempting to update user with id: " + userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            logger.error("User not found with ID: " + userId);
            return new NotFoundException("User not found");
        });

        user.setUsername(userDto.username());
        user.setPassword(encoder.encode(userDto.password()));

        User updatedUser = userRepository.save(user);

        logger.info("Updating user object");
        return UserResponse.convert(updatedUser);
    }

    /**
     * Deletes a user from the repository by their ID.
     *
     * @param userId The ID of the user to delete.
     * @return A string message indicating the result of the operation.
     * @throws NotFoundException if the user with the given ID is not found.
     */
    public String deleteUser(String userId) {
        logger.debug("Attempting to delete user with id: " + userId);
        if(userRepository.existsById(userId)){
            userRepository.deleteById(userId);
            logger.info("User has deleted with id: " + userId);
            return "User has deleted with id: " + userId;
        }
        else{
            logger.warn("Delete operation failed: User not found with id: " + userId);
            return "User not found with id: " + userId;
        }
    }

}
