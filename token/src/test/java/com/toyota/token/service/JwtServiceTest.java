package com.toyota.token.service;

import com.toyota.token.entity.Role;
import com.toyota.token.entity.User;
import com.toyota.token.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.stream.Collectors;

import static com.toyota.token.constant.Constant.SECRET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {
    private UserRepository userRepository;
    private JwtService jwtService;
    private Jws<Claims> jws;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        jws = Mockito.mock(Jws.class);
        jwtService = new JwtService(userRepository);
    }

    @Test
    void testValidateToken_whenTokenIsValid_shouldDoNothing() {
        Role role = generateRole();
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        User user = generateUser(role);

        Date issuedAt = new Date();
        Date validUntil = new Date(issuedAt.getTime() + 3600000);

        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("authorities", roles.stream().map(roletmp -> {
            return roletmp.getRolename();
        }).collect(Collectors.toList()));

        String FinalJws =  Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(validUntil)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();


        assertDoesNotThrow(() -> {
            jwtService.validateToken(FinalJws);
        });
    }

    @Test
    void testValidateToken_whenTokenIsNotValid_shouldThrowException() {
        String dummyToken = "asdadsfa";

        assertThrows(Exception.class, () -> {
            jwtService.validateToken(dummyToken);
        });
    }

    @Test //IS IT POSSIBLE THAT TWO PARTS IN ONE TEST FUNCTION POSSIBLE GENERATE-CREATE ?
    void testCreateToken_whenUserIsAvailable_shouldReturnToken() {
        Role role = generateRole();
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        User user = generateUser(role);

        Date issuedAt = new Date();
        Date validUntil = new Date(issuedAt.getTime() + 3600000);

        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("authorities", roles.stream().map(roletmp -> {
            return roletmp.getRolename();
        }).collect(Collectors.toList()));

        String expectedJws =  Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(validUntil)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

        String result = jwtService.createToken(user.getUsername() , roles);
        assertEquals(expectedJws, result);
    }

    @Test //IS IT POSSIBLE THAT TWO PARTS IN ONE TEST FUNCTION POSSIBLE GENERATE-CREATE ?
    void testGenerateToken_whenUserIsNotAvailable_shouldThrowException() {
        String username = "";

        when(userRepository.findByUsername(username)).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> {
            jwtService.generateToken(username);
        });
    }

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
}