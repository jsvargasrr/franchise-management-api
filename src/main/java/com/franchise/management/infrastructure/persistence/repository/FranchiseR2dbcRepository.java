package com.franchise.management.infrastructure.persistence.repository;

import com.franchise.management.infrastructure.persistence.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FranchiseR2dbcRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {}
