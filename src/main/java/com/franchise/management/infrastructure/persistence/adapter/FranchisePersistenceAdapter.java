package com.franchise.management.infrastructure.persistence.adapter;

import com.franchise.management.application.port.out.FranchisePersistencePort;
import com.franchise.management.domain.Franchise;
import com.franchise.management.domain.exception.NotFoundException;
import com.franchise.management.infrastructure.persistence.entity.FranchiseEntity;
import com.franchise.management.infrastructure.persistence.repository.FranchiseR2dbcRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Profile("!phase1")
public class FranchisePersistenceAdapter implements FranchisePersistencePort {

    private final FranchiseR2dbcRepository repository;

    public FranchisePersistenceAdapter(FranchiseR2dbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = new FranchiseEntity(franchise.id(), franchise.name());
        return repository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Mono<Void> updateName(Long id, String name) {
        return repository
                .findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada")))
                .flatMap(e -> repository.save(new FranchiseEntity(e.id(), name)))
                .then();
    }

    private Franchise toDomain(FranchiseEntity e) {
        return new Franchise(e.id(), e.name());
    }
}
