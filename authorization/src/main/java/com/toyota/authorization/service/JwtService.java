package com.toyota.authorization.service;

import com.toyota.authorization.entity.Role;
import com.toyota.authorization.entity.User;
import com.toyota.authorization.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.toyota.authorization.constant.Constant.SECRET;

@Component
public class JwtService {
    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateToken(final String token){
        Jwts.parserBuilder().setSigningKey(SECRET).build().parseClaimsJws(token);
    }

    public String generateToken(String userName) {
        Optional<User> user = userRepository.findByUsername(userName);
        Optional<List<Role>> roles = Optional.ofNullable(user.get().getRoles());
        return createToken(userName, roles.get());
    }

    public String createToken(String username, List<Role> roles) {

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("authorities", roles.stream().map(role -> {
            return role.getRolename();
        }).collect(Collectors.toList()));


        Date issuedAt = new Date();
        Date validUntil = new Date(issuedAt.getTime() + 3600000);

        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(validUntil)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();

    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET).build().parseClaimsJws(token).getBody();
        String username = claims.getSubject();

        return username;
    }

}
