package com.franchise.management.application.service;

import com.franchise.management.application.port.out.BranchPersistencePort;
import com.franchise.management.application.port.out.FranchisePersistencePort;
import com.franchise.management.application.port.out.MaxStockCachePort;
import com.franchise.management.application.port.out.ProductPersistencePort;
import com.franchise.management.domain.Branch;
import com.franchise.management.domain.Franchise;
import com.franchise.management.domain.MaxStockProduct;
import com.franchise.management.domain.Product;
import com.franchise.management.domain.exception.NotFoundException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Profile("!phase1")
public class FranchiseManagementService {

    private final FranchisePersistencePort franchises;
    private final BranchPersistencePort branches;
    private final ProductPersistencePort products;
    private final MaxStockCachePort maxStockCache;

    public FranchiseManagementService(
            FranchisePersistencePort franchises,
            BranchPersistencePort branches,
            ProductPersistencePort products,
            MaxStockCachePort maxStockCache) {
        this.franchises = franchises;
        this.branches = branches;
        this.products = products;
        this.maxStockCache = maxStockCache;
    }

    public Mono<Franchise> createFranchise(String name) {
        return franchises.save(new Franchise(null, name));
    }

    public Mono<Franchise> updateFranchiseName(Long franchiseId, String name) {
        return franchises
                .findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada")))
                .flatMap(f -> franchises.updateName(f.id(), name).then(franchises.findById(franchiseId)));
    }

    public Mono<Branch> addBranch(Long franchiseId, String name) {
        return franchises
                .findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada")))
                .flatMap(f -> branches.save(new Branch(null, f.id(), name)));
    }

    public Mono<Branch> updateBranchName(Long franchiseId, Long branchId, String name) {
        return branches
                .findByIdAndFranchiseId(branchId, franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no encontrada")))
                .flatMap(b -> branches.updateName(franchiseId, branchId, name).then(branches.findByIdAndFranchiseId(branchId, franchiseId)));
    }

    public Mono<Product> addProduct(Long franchiseId, Long branchId, String name, int stock) {
        return branches
                .findByIdAndFranchiseId(branchId, franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no encontrada")))
                .flatMap(b -> products
                        .save(new Product(null, b.id(), name, stock))
                        .flatMap(p -> maxStockCache.invalidate(franchiseId).thenReturn(p)));
    }

    public Mono<Void> deleteProduct(Long franchiseId, Long branchId, Long productId) {
        return branches
                .findByIdAndFranchiseId(branchId, franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no encontrada")))
                .flatMap(b -> products
                        .findByIdAndBranchId(productId, b.id())
                        .switchIfEmpty(Mono.error(new NotFoundException("Producto no encontrado")))
                        .flatMap(p -> products
                                .deleteByIdAndBranchId(p.id(), b.id())
                                .then(maxStockCache.invalidate(franchiseId))));
    }

    public Mono<Product> updateProductStock(Long franchiseId, Long branchId, Long productId, int stock) {
        if (stock < 0) {
            return Mono.error(new IllegalArgumentException("El stock no puede ser negativo"));
        }
        return branches
                .findByIdAndFranchiseId(branchId, franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no encontrada")))
                .flatMap(b -> products
                        .findByIdAndBranchId(productId, b.id())
                        .switchIfEmpty(Mono.error(new NotFoundException("Producto no encontrado")))
                        .flatMap(p -> products
                                .updateStock(b.id(), p.id(), stock)
                                .then(maxStockCache.invalidate(franchiseId))
                                .then(products.findByIdAndBranchId(productId, b.id()))));
    }

    public Mono<Product> updateProductName(Long franchiseId, Long branchId, Long productId, String name) {
        return branches
                .findByIdAndFranchiseId(branchId, franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no encontrada")))
                .flatMap(b -> products
                        .findByIdAndBranchId(productId, b.id())
                        .switchIfEmpty(Mono.error(new NotFoundException("Producto no encontrado")))
                        .flatMap(p -> products
                                .updateName(b.id(), p.id(), name)
                                .then(maxStockCache.invalidate(franchiseId))
                                .then(products.findByIdAndBranchId(productId, b.id()))));
    }

    public Flux<MaxStockProduct> maxStockByBranchForFranchise(Long franchiseId) {
        return franchises
                .findById(franchiseId)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada")))
                .flatMapMany(f -> maxStockCache
                        .getOptional(f.id())
                        .flatMapMany(opt -> opt.map(Flux::fromIterable)
                                .orElseGet(() -> products
                                        .findMaxStockByBranchForFranchise(f.id())
                                        .collectList()
                                        .flatMapMany(list -> maxStockCache
                                                .put(f.id(), list)
                                                .thenMany(Flux.fromIterable(list))))));
    }
}
