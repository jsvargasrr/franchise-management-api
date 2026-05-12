package com.franchise.management.infrastructure.persistence.repository;

import com.franchise.management.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductR2dbcRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    Mono<ProductEntity> findByIdAndBranchId(Long id, Long branchId);
}
