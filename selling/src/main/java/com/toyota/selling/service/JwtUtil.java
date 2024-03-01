package com.toyota.selling.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static Logger logger = LogManager.getLogger(JwtUtil.class);

    public String getUsernameFromToken(String token){

        Claims claims = Jwts.parser()
                .setSigningKey("5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437")
                .parseClaimsJws(token)
                .getBody();
        logger.info(" " + claims.getSubject().toString());
        return claims.getSubject().toString();
    }
}
