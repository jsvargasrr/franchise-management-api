package com.franchise.management.presentation.dto;

import com.franchise.management.domain.Franchise;

public record FranchiseResponse(Long id, String name) {

    public static FranchiseResponse from(Franchise f) {
        return new FranchiseResponse(f.id(), f.name());
    }
}
