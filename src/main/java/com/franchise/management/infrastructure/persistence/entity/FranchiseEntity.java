package com.franchise.management.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("franchise")
public record FranchiseEntity(@Id Long id, String name) {}
