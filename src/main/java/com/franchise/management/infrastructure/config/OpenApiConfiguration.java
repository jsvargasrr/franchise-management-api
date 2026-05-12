package com.franchise.management.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!phase1")
public class OpenApiConfiguration {

    @Bean
    public OpenAPI franchiseManagementOpenApi(
            @Value("${app.api.title:Franchise Management API}") String title,
            @Value("${app.api.version:1.0.0}") String version,
            @Value("${app.api.description:API reactiva para franquicias, sucursales y stock.}") String description) {
        return new OpenAPI()
                .info(new Info().title(title).version(version).description(description))
                .tags(List.of(
                        new Tag().name("Franquicias").description("Gestión de franquicias y sucursales"),
                        new Tag().name("Productos").description("Productos y stock por sucursal"),
                        new Tag().name("Informes").description("Consultas agregadas")));
    }
}
