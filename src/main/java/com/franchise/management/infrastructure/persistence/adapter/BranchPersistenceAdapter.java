package com.franchise.management.infrastructure.persistence.adapter;

import com.franchise.management.application.port.out.BranchPersistencePort;
import com.franchise.management.domain.Branch;
import com.franchise.management.domain.exception.NotFoundException;
import com.franchise.management.infrastructure.persistence.entity.BranchEntity;
import com.franchise.management.infrastructure.persistence.repository.BranchR2dbcRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Profile("!phase1")
public class BranchPersistenceAdapter implements BranchPersistencePort {

    private final BranchR2dbcRepository repository;

    public BranchPersistenceAdapter(BranchR2dbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity entity = new BranchEntity(branch.id(), branch.franchiseId(), branch.name());
        return repository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<Branch> findByIdAndFranchiseId(Long branchId, Long franchiseId) {
        return repository.findByIdAndFranchiseId(branchId, franchiseId).map(this::toDomain);
    }

    @Override
    public Mono<Void> updateName(Long franchiseId, Long branchId, String name) {
        return repository
                .findByIdAndFranchiseId(branchId, franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no encontrada")))
                .flatMap(e -> repository.save(new BranchEntity(e.id(), e.franchiseId(), name)))
                .then();
    }

    private Branch toDomain(BranchEntity e) {
        return new Branch(e.id(), e.franchiseId(), e.name());
    }
}
