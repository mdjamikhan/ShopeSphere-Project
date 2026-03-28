package com.lpu.shopsphere.apigateway.security;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher matcher = new AntPathMatcher();

 
    private final List<String> publicAuthPatterns = List.of(
        "/gateway/auth/signup",
        "/gateway/auth/signup/**",
        "/gateway/auth/login",
        "/gateway/auth/login/**"
    );

     private final List<String> publicCatalogReadPatterns = List.of(
        "/gateway/catalog/products",
        "/gateway/catalog/products/featured",
        "/gateway/catalog/products/**"
    );

    // Swagger/OpenAPI endpoints must be public, because Swagger UI runs in browser
    // and fetches `/v3/api-docs` from the client side.
    private final List<String> publicSwaggerUiPatterns = List.of(
        "/swagger-ui.html",
        "/swagger-ui/index.html",
        "/swagger-ui/**",
        "/v3/api-docs",
        "/v3/api-docs/**"
    );

    private final List<String> publicGatewayApiDocsPatterns = List.of(
        "/gateway/auth/v3/api-docs",
        "/gateway/catalog/v3/api-docs",
        "/gateway/orders/v3/api-docs",
        "/gateway/admin/v3/api-docs",
        "/gateway/auth/v3/api-docs/**",
        "/gateway/catalog/v3/api-docs/**",
        "/gateway/orders/v3/api-docs/**",
        "/gateway/admin/v3/api-docs/**"
    );
    

    public JwtAuthGlobalFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public int getOrder() {
        return -100;  
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod() != null
            ? exchange.getRequest().getMethod().name()
            : "";

          if (isPublic(method, path)) {
            return chain.filter(exchange);
        }

        boolean isAdminPath = matcher.match("/gateway/admin/**", path);

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

         if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

         if (!jwtUtil.validate(token)) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        Claims claims;

         try {
            claims = jwtUtil.parseClaims(token);
        } catch (Exception e) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Token parsing failed");
        }

        String email = claims.getSubject();

         String role = claims.get("role", String.class);

        if (!StringUtils.hasText(role)) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Role not found in token");
        }

         if (isAdminPath && !"ROLE_ADMIN".equals(role)) {
            return writeError(exchange, HttpStatus.FORBIDDEN, "Access denied: Admin only");
        }

         ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                )
                .build();

        return chain.filter(mutatedExchange);
    }

     private boolean isPublic(String method, String path) {
         if (publicAuthPatterns.stream().anyMatch(p -> matcher.match(p, path))) {
            return true;
        }

         if (publicSwaggerUiPatterns.stream().anyMatch(p -> matcher.match(p, path))) {
            return true;
        }

        if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
            return false;
        }

        return publicCatalogReadPatterns.stream().anyMatch(p -> matcher.match(p, path))
            || publicGatewayApiDocsPatterns.stream().anyMatch(p -> matcher.match(p, path));
    }

     private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
                status.value(),
                status.getReasonPhrase(),
                message
        );

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}