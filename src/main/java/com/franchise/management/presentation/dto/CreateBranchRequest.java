package com.franchise.management.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBranchRequest(@NotBlank String name) {}
