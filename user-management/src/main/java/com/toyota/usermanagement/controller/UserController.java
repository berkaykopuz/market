package com.toyota.usermanagement.controller;

import com.toyota.usermanagement.dto.UserDto;
import com.toyota.usermanagement.dto.UserResponse;
import com.toyota.usermanagement.entity.Role;
import com.toyota.usermanagement.entity.User;
import com.toyota.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/roleregister")
    public String addNewRole(@RequestBody Role role) {
        return userService.saveRole(role);
    }
    @PostMapping("/register")
    public String addNewUser(@RequestBody User user, @RequestParam String rolename) {
        return userService.saveUser(user,rolename);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }


    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @PutMapping("update/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") String userId, @RequestBody UserDto userDto){
        return new ResponseEntity<>(userService.updateUser(userDto, userId), HttpStatus.OK);
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String userId){
        return new ResponseEntity<>(userService.deleteUser(userId),HttpStatus.OK);
    }

}
