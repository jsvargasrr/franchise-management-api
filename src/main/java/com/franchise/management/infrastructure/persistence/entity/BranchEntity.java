package com.franchise.management.infrastructure.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("branch")
public record BranchEntity(@Id Long id, @Column("franchise_id") Long franchiseId, String name) {}
