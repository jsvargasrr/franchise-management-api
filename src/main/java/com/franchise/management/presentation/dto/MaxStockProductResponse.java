package com.franchise.management.presentation.dto;

import com.franchise.management.domain.MaxStockProduct;

public record MaxStockProductResponse(
        long branchId, String branchName, long productId, String productName, int stock) {

    public static MaxStockProductResponse from(MaxStockProduct p) {
        return new MaxStockProductResponse(
                p.branchId(), p.branchName(), p.productId(), p.productName(), p.stock());
    }
}
