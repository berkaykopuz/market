package com.toyota.gateway.filter;

import com.toyota.gateway.exception.UnauthorizedException;
import com.toyota.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class SellingFilter extends AbstractGatewayFilterFactory<SellingFilter.Config> {
    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public SellingFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }


    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new UnauthorizedException("Missing authorization header. Access denied");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {

                    jwtUtil.validateToken(authHeader);

                    // check if user has CASHIER role
                    if (!jwtUtil.getRolesFromToken(authHeader).contains("CASHIER")) {
                        throw new UnauthorizedException("User does not have CASHIER role. Access denied");
                    }

                } catch (Exception e) {
                    System.out.println("invalid access...!");
                    throw e;
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
