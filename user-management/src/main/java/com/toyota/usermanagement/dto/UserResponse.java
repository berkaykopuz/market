package com.toyota.usermanagement.dto;

import com.toyota.usermanagement.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public record UserResponse(String id,
                           String username,
                           List<String> roles) {
    public static UserResponse convert(User from){
        return new UserResponse(from.getId(),
                from.getUsername(),
                from.getRoles().stream().map(r -> r.getRolename()).collect(Collectors.toList()));
    }
}
