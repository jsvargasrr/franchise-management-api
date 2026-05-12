package com.franchise.management.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFranchiseRequest(@NotBlank String name) {}
