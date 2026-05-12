package com.franchise.management.presentation.dto;

import com.franchise.management.domain.Branch;

public record BranchResponse(Long id, Long franchiseId, String name) {

    public static BranchResponse from(Branch b) {
        return new BranchResponse(b.id(), b.franchiseId(), b.name());
    }
}
