package com.franchise.management.presentation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(@NotBlank String name, @Min(0) int stock) {}
