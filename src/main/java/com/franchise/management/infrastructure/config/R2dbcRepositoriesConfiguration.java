package com.franchise.management.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@Profile("!phase1")
@EnableR2dbcRepositories(basePackages = "com.franchise.management.infrastructure.persistence.repository")
public class R2dbcRepositoriesConfiguration {}
