package com.franchise.management.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.franchise.management.application.port.out.BranchPersistencePort;
import com.franchise.management.application.port.out.FranchisePersistencePort;
import com.franchise.management.application.port.out.MaxStockCachePort;
import com.franchise.management.application.port.out.ProductPersistencePort;
import com.franchise.management.domain.Branch;
import com.franchise.management.domain.Franchise;
import com.franchise.management.domain.MaxStockProduct;
import com.franchise.management.domain.Product;
import com.franchise.management.domain.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranchiseManagementServiceTest {

    @Mock
    private FranchisePersistencePort franchises;

    @Mock
    private BranchPersistencePort branches;

    @Mock
    private ProductPersistencePort products;

    @Mock
    private MaxStockCachePort maxStockCache;

    private FranchiseManagementService service;

    @BeforeEach
    void setUp() {
        service = new FranchiseManagementService(franchises, branches, products, maxStockCache);
    }

    @Test
    void createFranchiseDelegatesToPort() {
        Franchise saved = new Franchise(1L, "X");
        when(franchises.save(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(service.createFranchise("X"))
                .assertNext(f -> assertThat(f.id()).isEqualTo(1L))
                .verifyComplete();

        verify(franchises).save(new Franchise(null, "X"));
    }

    @Test
    void maxStockByBranchRequiresExistingFranchise() {
        when(franchises.findById(9L)).thenReturn(Mono.empty());

        StepVerifier.create(service.maxStockByBranchForFranchise(9L).then())
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void maxStockByBranchDelegatesToProductPortOnCacheMiss() {
        when(franchises.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "F")));
        when(maxStockCache.getOptional(1L)).thenReturn(Mono.just(Optional.empty()));
        when(products.findMaxStockByBranchForFranchise(1L))
                .thenReturn(Flux.fromIterable(List.of(new MaxStockProduct(2L, "B", 3L, "P", 10))));
        when(maxStockCache.put(eq(1L), anyList())).thenReturn(Mono.empty());

        StepVerifier.create(service.maxStockByBranchForFranchise(1L).collectList())
                .assertNext(list -> assertThat(list).hasSize(1))
                .verifyComplete();

        verify(products).findMaxStockByBranchForFranchise(1L);
        verify(maxStockCache).put(eq(1L), anyList());
    }

    @Test
    void maxStockByBranchUsesCacheWhenPresent() {
        when(franchises.findById(1L)).thenReturn(Mono.just(new Franchise(1L, "F")));
        List<MaxStockProduct> cached = List.of(new MaxStockProduct(2L, "B", 3L, "P", 10));
        when(maxStockCache.getOptional(1L)).thenReturn(Mono.just(Optional.of(cached)));

        StepVerifier.create(service.maxStockByBranchForFranchise(1L).collectList())
                .assertNext(list -> assertThat(list).isEqualTo(cached))
                .verifyComplete();

        verify(products, never()).findMaxStockByBranchForFranchise(any());
        verify(maxStockCache, never()).put(any(), anyList());
    }

    @Test
    void addProductValidatesBranchOwnershipAndInvalidatesCache() {
        when(branches.findByIdAndFranchiseId(5L, 1L)).thenReturn(Mono.just(new Branch(5L, 1L, "S")));
        when(products.save(any(Product.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0, Product.class)));
        when(maxStockCache.invalidate(1L)).thenReturn(Mono.empty());

        StepVerifier.create(service.addProduct(1L, 5L, "Item", 3))
                .assertNext(p -> {
                    assertThat(p.branchId()).isEqualTo(5L);
                    assertThat(p.name()).isEqualTo("Item");
                    assertThat(p.stock()).isEqualTo(3);
                })
                .verifyComplete();

        verify(products).save(new Product(null, 5L, "Item", 3));
        verify(maxStockCache).invalidate(1L);
    }

    @Test
    void updateProductStockRejectsNegative() {
        StepVerifier.create(service.updateProductStock(1L, 5L, 9L, -1))
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}
