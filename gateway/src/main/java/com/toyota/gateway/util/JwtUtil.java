package com.toyota.gateway.util;

import com.toyota.gateway.exception.UnauthenticatedException;
import com.toyota.gateway.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.toyota.gateway.constant.Constant.SECRET;

@Component
public class JwtUtil {
    private static Logger logger = LogManager.getLogger(JwtUtil.class);

    public boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET).build().parseClaimsJws(token);
            logger.info("Token has validated");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Expired token");
            throw new UnauthenticatedException("Expired token");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            logger.warn("Invalid token");
            throw new UnauthenticatedException("Invalid token");
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
