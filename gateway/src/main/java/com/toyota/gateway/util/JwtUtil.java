package com.toyota.gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.toyota.gateway.constant.Constant.SECRET;

@Component
public class JwtUtil {

    public boolean validateToken(final String token) {
        try{
            Jwts.parserBuilder().setSigningKey(SECRET).build().parseClaimsJws(token);
            return true;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<String> getRolesFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();

        List<String> roles = new ArrayList<>();
        if (claims.get("authorities") != null) {
            roles = (List<String>) claims.get("authorities");
        }
        return roles;
    }
}
