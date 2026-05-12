package com.franchise.management.application.port.out;

import com.franchise.management.domain.MaxStockProduct;
import java.util.List;
import java.util.Optional;
import reactor.core.publisher.Mono;

/**
 * Caché reactiva para el informe de máximo stock por sucursal. {@code Optional.empty()} indica miss.
 */
public interface MaxStockCachePort {

    Mono<Optional<List<MaxStockProduct>>> getOptional(Long franchiseId);

    Mono<Void> put(Long franchiseId, List<MaxStockProduct> items);

    Mono<Void> invalidate(Long franchiseId);
}
