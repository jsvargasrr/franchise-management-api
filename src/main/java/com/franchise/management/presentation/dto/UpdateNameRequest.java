package com.franchise.management.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameRequest(@NotBlank String name) {}
