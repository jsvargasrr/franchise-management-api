package com.franchise.management.application.port.out;

import com.franchise.management.domain.MaxStockProduct;
import com.franchise.management.domain.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductPersistencePort {

    Mono<Product> save(Product product);

    Mono<Product> findByIdAndBranchId(Long productId, Long branchId);

    Mono<Void> deleteByIdAndBranchId(Long productId, Long branchId);

    Mono<Void> updateStock(Long branchId, Long productId, int stock);

    Mono<Void> updateName(Long branchId, Long productId, String name);

    Flux<MaxStockProduct> findMaxStockByBranchForFranchise(Long franchiseId);
}
