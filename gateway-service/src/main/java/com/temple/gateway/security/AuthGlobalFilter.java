package com.temple.gateway.security;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtVerifier jwtVerifier;

    public AuthGlobalFilter(JwtVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                            org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
    	
    	System.out.println("Coming here");

    	  // ✅ 0) Allow CORS preflight to pass through
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequest().getMethod().toString())) {
            return chain.filter(exchange);
        }
        
        String path = exchange.getRequest().getURI().getPath();

        // ✅ Public endpoints
     // ✅ Public endpoints
        if (path.startsWith("/auth/")
                || path.startsWith("/oauth2/")
                || path.startsWith("/login/")
                || path.startsWith("/actuator/")) {
            return chain.filter(exchange);
        }

        // Require JWT for all other routes
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = auth.substring("Bearer ".length()).trim();

        try {
            JwtVerifier.DecodedToken decoded = jwtVerifier.verify(token);

            // Forward user context to downstream services
            ServerWebExchange mutated = exchange.mutate()
                .request(r -> r.headers(h -> {
                    h.remove("X-User-Email");
                    h.remove("X-User-Role");
                    h.add("X-User-Email", decoded.email());
                    h.add("X-User-Role", decoded.role());
                }))
                .build();

            return chain.filter(mutated);

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        // run early
        return -1;
    }
}
