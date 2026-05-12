package com.franchise.management.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ApiKeyWebFilterTest {

    @Test
    void filterBypassesWhenSecurityDisabled() {
        AppSecurityProperties props = new AppSecurityProperties();
        props.setEnabled(false);
        ApiKeyWebFilter filter = new ApiKeyWebFilter(props);
        MockServerWebExchange exchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/franchises").build());
        boolean[] chainCalled = {false};
        WebFilterChain chain = e -> {
            chainCalled[0] = true;
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(chainCalled[0]).isTrue();
    }

    @Test
    void rejectsWhenKeyMissingOrWrong() {
        AppSecurityProperties props = new AppSecurityProperties();
        props.setEnabled(true);
        props.setApiKey("secret");
        ApiKeyWebFilter filter = new ApiKeyWebFilter(props);
        MockServerWebExchange exchange =
                MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/franchises").build());
        WebFilterChain chain = e -> Mono.empty();

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void acceptsMatchingHeader() {
        AppSecurityProperties props = new AppSecurityProperties();
        props.setEnabled(true);
        props.setApiKey("secret");
        ApiKeyWebFilter filter = new ApiKeyWebFilter(props);
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/v1/franchises")
                        .header(ApiKeyWebFilter.API_KEY_HEADER, "secret")
                        .build());
        boolean[] chainCalled = {false};
        WebFilterChain chain = e -> {
            chainCalled[0] = true;
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

        assertThat(chainCalled[0]).isTrue();
    }

    @Test
    void pingAndActuatorArePublic() {
        AppSecurityProperties props = new AppSecurityProperties();
        props.setEnabled(true);
        props.setApiKey("secret");
        assertThat(ApiKeyWebFilter.isPublic(MockServerHttpRequest.get("/api/v1/ping").build()))
                .isTrue();
        assertThat(ApiKeyWebFilter.isPublic(MockServerHttpRequest.get("/actuator/health").build()))
                .isTrue();
        assertThat(ApiKeyWebFilter.isPublic(MockServerHttpRequest.get("/api/v1/docs/swagger-ui.html").build()))
                .isTrue();
    }
}
