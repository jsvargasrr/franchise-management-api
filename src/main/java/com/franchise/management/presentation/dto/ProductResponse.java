package com.franchise.management.presentation.dto;

import com.franchise.management.domain.Product;

public record ProductResponse(Long id, Long branchId, String name, int stock) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(p.id(), p.branchId(), p.name(), p.stock());
    }
}
