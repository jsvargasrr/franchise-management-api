package com.franchise.management.presentation;

import com.franchise.management.application.service.FranchiseManagementService;
import com.franchise.management.presentation.dto.BranchResponse;
import com.franchise.management.presentation.dto.CreateBranchRequest;
import com.franchise.management.presentation.dto.CreateFranchiseRequest;
import com.franchise.management.presentation.dto.CreateProductRequest;
import com.franchise.management.presentation.dto.FranchiseResponse;
import com.franchise.management.presentation.dto.MaxStockProductResponse;
import com.franchise.management.presentation.dto.ProductResponse;
import com.franchise.management.presentation.dto.UpdateNameRequest;
import com.franchise.management.presentation.dto.UpdateStockRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/franchises")
@Validated
@Profile("!phase1")
@Tag(name = "Franquicias", description = "Franquicias, sucursales, productos e informes de stock")
public class FranchiseRestController {

    private final FranchiseManagementService franchiseManagementService;

    public FranchiseRestController(FranchiseManagementService franchiseManagementService) {
        this.franchiseManagementService = franchiseManagementService;
    }

    @PostMapping
    public Mono<ResponseEntity<FranchiseResponse>> createFranchise(@Valid @RequestBody CreateFranchiseRequest request) {
        return franchiseManagementService
                .createFranchise(request.name())
                .map(f -> ResponseEntity.created(URI.create("/api/v1/franchises/" + f.id()))
                        .body(FranchiseResponse.from(f)));
    }

    @PatchMapping("/{franchiseId}")
    public Mono<FranchiseResponse> updateFranchiseName(
            @PathVariable @Positive Long franchiseId, @Valid @RequestBody UpdateNameRequest request) {
        return franchiseManagementService
                .updateFranchiseName(franchiseId, request.name())
                .map(FranchiseResponse::from);
    }

    @PostMapping("/{franchiseId}/branches")
    public Mono<ResponseEntity<BranchResponse>> addBranch(
            @PathVariable @Positive Long franchiseId, @Valid @RequestBody CreateBranchRequest request) {
        return franchiseManagementService
                .addBranch(franchiseId, request.name())
                .map(b -> ResponseEntity.created(URI.create(
                                "/api/v1/franchises/" + franchiseId + "/branches/" + b.id()))
                        .body(BranchResponse.from(b)));
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}")
    public Mono<BranchResponse> updateBranchName(
            @PathVariable @Positive Long franchiseId,
            @PathVariable @Positive Long branchId,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseManagementService
                .updateBranchName(franchiseId, branchId, request.name())
                .map(BranchResponse::from);
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    public Mono<ResponseEntity<ProductResponse>> addProduct(
            @PathVariable @Positive Long franchiseId,
            @PathVariable @Positive Long branchId,
            @Valid @RequestBody CreateProductRequest request) {
        return franchiseManagementService
                .addProduct(franchiseId, branchId, request.name(), request.stock())
                .map(p -> ResponseEntity.created(URI.create(
                                "/api/v1/franchises/"
                                        + franchiseId
                                        + "/branches/"
                                        + branchId
                                        + "/products/"
                                        + p.id()))
                        .body(ProductResponse.from(p)));
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public Mono<ResponseEntity<Void>> deleteProduct(
            @PathVariable @Positive Long franchiseId,
            @PathVariable @Positive Long branchId,
            @PathVariable @Positive Long productId) {
        return franchiseManagementService
                .deleteProduct(franchiseId, branchId, productId)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public Mono<ProductResponse> updateProductStock(
            @PathVariable @Positive Long franchiseId,
            @PathVariable @Positive Long branchId,
            @PathVariable @Positive Long productId,
            @Valid @RequestBody UpdateStockRequest request) {
        return franchiseManagementService
                .updateProductStock(franchiseId, branchId, productId, request.stock())
                .map(ProductResponse::from);
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public Mono<ProductResponse> updateProductName(
            @PathVariable @Positive Long franchiseId,
            @PathVariable @Positive Long branchId,
            @PathVariable @Positive Long productId,
            @Valid @RequestBody UpdateNameRequest request) {
        return franchiseManagementService
                .updateProductName(franchiseId, branchId, productId, request.name())
                .map(ProductResponse::from);
    }

    @GetMapping("/{franchiseId}/reports/max-stock-by-branch")
    public Flux<MaxStockProductResponse> maxStockByBranch(@PathVariable @Positive Long franchiseId) {
        return franchiseManagementService
                .maxStockByBranchForFranchise(franchiseId)
                .map(MaxStockProductResponse::from);
    }
}
