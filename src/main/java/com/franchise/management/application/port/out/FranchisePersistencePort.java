package com.franchise.management.application.port.out;

import com.franchise.management.domain.Franchise;
import reactor.core.publisher.Mono;

public interface FranchisePersistencePort {

    Mono<Franchise> save(Franchise franchise);

    Mono<Franchise> findById(Long id);

    Mono<Void> updateName(Long id, String name);
}
