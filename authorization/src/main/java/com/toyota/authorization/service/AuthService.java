package com.toyota.authorization.service;

import com.toyota.authorization.repository.RoleRepository;
import com.toyota.authorization.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static Logger logger = LogManager.getLogger(AuthService.class);
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String generateToken(String username) {
        logger.info("Auth token has generated");
        return jwtService.generateToken(username);
    }

    public void validateToken(String token) {
        logger.info("Auth token has validated");
        jwtService.validateToken(token);
    }

}
