package com.franchise.management.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("product")
public record ProductEntity(@Id Long id, @Column("branch_id") Long branchId, String name, int stock) {}
