package com.franchise.management.presentation;

import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ApiRoutesConfiguration {

    @Bean
    public RouterFunction<ServerResponse> apiV1Routes() {
        return route(
                GET("/api/v1/ping"),
                request -> ServerResponse.ok().bodyValue(Map.of("service", "franchise-management-api", "status", "ok")));
    }
}
