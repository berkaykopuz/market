package com.toyota.usermanagement.dto;

import com.toyota.usermanagement.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public record UserDto(String id,
        String username,
        String password,
        List<String> roles) {

    public static UserDto convert(User from){
        return new UserDto(from.getId(),
                from.getUsername(),
                from.getPassword(),
                from.getRoles().stream().map(r -> r.getRolename()).collect(Collectors.toList()));
    }
}
