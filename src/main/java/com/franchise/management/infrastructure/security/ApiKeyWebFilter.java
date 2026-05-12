package com.franchise.management.infrastructure.security;

import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Seguridad ligera opcional (Fase 5): si {@code app.security.enabled=true} y hay
 * {@code app.security.api-key}, exige la cabecera {@code X-API-Key} en rutas no públicas.
 * No usa spring-boot-starter-security para no interferir con Actuator ni Swagger.
 */
@Component
@Profile("!phase1")
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class ApiKeyWebFilter implements WebFilter {

    public static final String API_KEY_HEADER = "X-API-Key";

    private final AppSecurityProperties security;

    public ApiKeyWebFilter(AppSecurityProperties security) {
        this.security = security;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!security.requiresApiKey()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        if (isPublic(request)) {
            return chain.filter(exchange);
        }
        String provided = request.getHeaders().getFirst(API_KEY_HEADER);
        if (security.getApiKey().equals(provided)) {
            return chain.filter(exchange);
        }
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "ApiKey realm=\"franchise-api\"");
        return exchange.getResponse().setComplete();
    }

    static boolean isPublic(ServerHttpRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod().name())) {
            return true;
        }
        String path = request.getURI().getPath();
        if (path.startsWith("/actuator")) {
            return true;
        }
        if ("/api/v1/ping".equals(path)) {
            return true;
        }
        if (path.startsWith("/api/v1/docs")) {
            return true;
        }
        if (path.startsWith("/webjars/")) {
            return true;
        }
        if (path.startsWith("/swagger-ui")) {
            return true;
        }
        return false;
    }
}
