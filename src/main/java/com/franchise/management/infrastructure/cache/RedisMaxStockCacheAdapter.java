package com.franchise.management.infrastructure.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franchise.management.application.port.out.MaxStockCachePort;
import com.franchise.management.domain.MaxStockProduct;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Profile("!phase1")
@ConditionalOnBean(ReactiveStringRedisTemplate.class)
public class RedisMaxStockCacheAdapter implements MaxStockCachePort {

    private static final String KEY_PREFIX = "franchise:maxstock:";

    private final ReactiveStringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public RedisMaxStockCacheAdapter(
            ReactiveStringRedisTemplate redis,
            ObjectMapper objectMapper,
            @Value("${app.cache.max-stock-ttl:60s}") Duration ttl) {
        this.redis = redis;
        this.objectMapper = objectMapper;
        this.ttl = ttl;
    }

    @Override
    public Mono<Optional<List<MaxStockProduct>>> getOptional(Long franchiseId) {
        String key = KEY_PREFIX + franchiseId;
        return redis.opsForValue()
                .get(key)
                .flatMap(json -> {
                    try {
                        List<MaxStockProduct> list =
                                objectMapper.readValue(json, new TypeReference<List<MaxStockProduct>>() {});
                        return Mono.just(Optional.of(list));
                    } catch (Exception e) {
                        return redis.delete(key).then(Mono.just(Optional.<List<MaxStockProduct>>empty()));
                    }
                })
                .defaultIfEmpty(Optional.empty());
    }

    @Override
    public Mono<Void> put(Long franchiseId, List<MaxStockProduct> items) {
        String key = KEY_PREFIX + franchiseId;
        try {
            String json = objectMapper.writeValueAsString(items);
            return redis.opsForValue().set(key, json, ttl).then();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<Void> invalidate(Long franchiseId) {
        return redis.delete(KEY_PREFIX + franchiseId).then();
    }
}
