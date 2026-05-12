package com.franchise.management.infrastructure.cache;

import com.franchise.management.application.port.out.MaxStockCachePort;
import com.franchise.management.domain.MaxStockProduct;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Profile("!phase1")
@ConditionalOnMissingBean(ReactiveStringRedisTemplate.class)
public class NoOpMaxStockCacheAdapter implements MaxStockCachePort {

    @Override
    public Mono<Optional<List<MaxStockProduct>>> getOptional(Long franchiseId) {
        return Mono.just(Optional.empty());
    }

    @Override
    public Mono<Void> put(Long franchiseId, List<MaxStockProduct> items) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> invalidate(Long franchiseId) {
        return Mono.empty();
    }
}
