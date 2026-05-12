package com.franchise.management.application.port.out;

import com.franchise.management.domain.Branch;
import reactor.core.publisher.Mono;

public interface BranchPersistencePort {

    Mono<Branch> save(Branch branch);

    Mono<Branch> findByIdAndFranchiseId(Long branchId, Long franchiseId);

    Mono<Void> updateName(Long franchiseId, Long branchId, String name);
}
