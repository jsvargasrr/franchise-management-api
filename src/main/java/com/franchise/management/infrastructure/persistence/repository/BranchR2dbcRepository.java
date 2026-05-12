package com.franchise.management.infrastructure.persistence.repository;

import com.franchise.management.infrastructure.persistence.entity.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BranchR2dbcRepository extends ReactiveCrudRepository<BranchEntity, Long> {

    Mono<BranchEntity> findByIdAndFranchiseId(Long id, Long franchiseId);
}
