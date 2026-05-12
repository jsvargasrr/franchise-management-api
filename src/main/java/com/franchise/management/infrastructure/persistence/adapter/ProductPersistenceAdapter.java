package com.franchise.management.infrastructure.persistence.adapter;

import com.franchise.management.application.port.out.ProductPersistencePort;
import com.franchise.management.domain.MaxStockProduct;
import com.franchise.management.domain.Product;
import com.franchise.management.infrastructure.persistence.entity.ProductEntity;
import com.franchise.management.infrastructure.persistence.repository.ProductR2dbcRepository;
import java.util.Objects;
import org.springframework.context.annotation.Profile;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Profile("!phase1")
public class ProductPersistenceAdapter implements ProductPersistencePort {

    private static final String MAX_STOCK_QUERY =
            """
            SELECT b.id AS branch_id, b.name AS branch_name,
                   p.id AS product_id, p.name AS product_name, p.stock AS stock
            FROM product p
            INNER JOIN branch b ON p.branch_id = b.id
            WHERE b.franchise_id = :franchiseId
              AND p.stock = (
                  SELECT COALESCE(MAX(p2.stock), 0) FROM product p2 WHERE p2.branch_id = b.id
              )
              AND EXISTS (SELECT 1 FROM product p3 WHERE p3.branch_id = b.id)
            """;

    private final ProductR2dbcRepository repository;
    private final DatabaseClient databaseClient;

    public ProductPersistenceAdapter(ProductR2dbcRepository repository, DatabaseClient databaseClient) {
        this.repository = repository;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = new ProductEntity(product.id(), product.branchId(), product.name(), product.stock());
        return repository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<Product> findByIdAndBranchId(Long productId, Long branchId) {
        return repository.findByIdAndBranchId(productId, branchId).map(this::toDomain);
    }

    @Override
    public Mono<Void> deleteByIdAndBranchId(Long productId, Long branchId) {
        return repository
                .findByIdAndBranchId(productId, branchId)
                .flatMap(repository::delete)
                .then();
    }

    @Override
    public Mono<Void> updateStock(Long branchId, Long productId, int stock) {
        return repository
                .findByIdAndBranchId(productId, branchId)
                .flatMap(e -> repository.save(new ProductEntity(e.id(), e.branchId(), e.name(), stock)))
                .then();
    }

    @Override
    public Mono<Void> updateName(Long branchId, Long productId, String name) {
        return repository
                .findByIdAndBranchId(productId, branchId)
                .flatMap(e -> repository.save(new ProductEntity(e.id(), e.branchId(), name, e.stock())))
                .then();
    }

    @Override
    public Flux<MaxStockProduct> findMaxStockByBranchForFranchise(Long franchiseId) {
        return databaseClient
                .sql(MAX_STOCK_QUERY)
                .bind("franchiseId", franchiseId)
                .map((row, meta) -> new MaxStockProduct(
                        Objects.requireNonNull(row.get("branch_id", Long.class)),
                        Objects.requireNonNull(row.get("branch_name", String.class)),
                        Objects.requireNonNull(row.get("product_id", Long.class)),
                        Objects.requireNonNull(row.get("product_name", String.class)),
                        Objects.requireNonNullElse(row.get("stock", Integer.class), 0)))
                .all();
    }

    private Product toDomain(ProductEntity e) {
        return new Product(e.id(), e.branchId(), e.name(), e.stock());
    }
}
