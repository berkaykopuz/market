package com.toyota.token.service;

import com.toyota.token.entity.Role;
import com.toyota.token.entity.User;
import com.toyota.token.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.apache.logging.log4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.toyota.token.constant.Constant.SECRET;

@Component
public class JwtService {
    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validates the JWT token's integrity and structure.
     *
     * @param token The JWT token to be validated.
     */
    public void validateToken(final String token){
        Jwts.parserBuilder().setSigningKey(SECRET).build().parseClaimsJws(token);
    }
    /**
     * Generates a JWT token for a user with the given username.
     *
     * @param userName The username for which the token will be generated.
     * @return A JWT token string for the user.
     */
    public String generateToken(String userName) {
        Optional<User> user = userRepository.findByUsername(userName);
        Optional<List<Role>> roles = Optional.ofNullable(user.get().getRoles());
        return createToken(userName, roles.get());
    }
    /**
     * Creates a JWT token with the specified username and roles.
     *
     * @param username The subject for whom the token is being created.
     * @param roles The roles that will be included in the token's claims.
     * @return A JWT token string that includes the user's roles.
     */
    public String createToken(String username, List<Role> roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("authorities", roles.stream().map(role -> {
            return role.getRolename();
        }).collect(Collectors.toList()));


        Date issuedAt = new Date();
        Date validUntil = new Date(issuedAt.getTime() + 36000000);

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(validUntil)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

    }

}
