package com.toyota.gateway.filter;

import com.toyota.gateway.exception.UnauthorizedException;
import com.toyota.gateway.util.JwtUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.function.Consumer;

@Component
public class ReportFilter extends AbstractGatewayFilterFactory<ReportFilter.Config> {
    public static Logger logger = LogManager.getLogger(ReportFilter.class);
    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public ReportFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    logger.error("Missing authorization header. Access denied");
                    throw new UnauthorizedException("Missing authorization header. Access denied");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    jwtUtil.validateToken(authHeader);

                    // check if user has MANAGER role
                    if (!jwtUtil.getRolesFromToken(authHeader).contains("MANAGER")) {
                        logger.error("User does not have MANAGER role. Access denied");
                        throw new UnauthorizedException("User does not have MANAGER role. Access denied");
                    }

                    logger.info("Token has verified.");

                } catch (Exception e) {
                    logger.error("Invalid access..!");
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
