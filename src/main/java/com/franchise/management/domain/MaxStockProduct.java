package com.franchise.management.domain;

public record MaxStockProduct(
        long branchId, String branchName, long productId, String productName, int stock) {}
