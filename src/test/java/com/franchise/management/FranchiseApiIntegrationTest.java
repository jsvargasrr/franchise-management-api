package com.franchise.management;

import static org.assertj.core.api.Assertions.assertThat;

import com.franchise.management.presentation.dto.BranchResponse;
import com.franchise.management.presentation.dto.CreateBranchRequest;
import com.franchise.management.presentation.dto.CreateFranchiseRequest;
import com.franchise.management.presentation.dto.CreateProductRequest;
import com.franchise.management.presentation.dto.FranchiseResponse;
import com.franchise.management.presentation.dto.MaxStockProductResponse;
import com.franchise.management.presentation.dto.ProductResponse;
import com.franchise.management.presentation.dto.UpdateStockRequest;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=testcontainers")
@AutoConfigureWebTestClient
@Testcontainers
@Tag("integration")
class FranchiseApiIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("franchise_db")
            .withUsername("franchise")
            .withPassword("franchise_secret");

    @DynamicPropertySource
    static void registerMysql(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.r2dbc.url",
                () -> String.format(
                        "r2dbc:mysql://%s:%d/%s?sslMode=DISABLED&tcpKeepAlive=true",
                        MYSQL.getHost(), MYSQL.getMappedPort(MySQLContainer.MYSQL_PORT), MYSQL.getDatabaseName()));
        registry.add("spring.r2dbc.username", MYSQL::getUsername);
        registry.add("spring.r2dbc.password", MYSQL::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void franchiseBranchProductLifecycleAndMaxStockReport() {
        FranchiseResponse franchise = webTestClient
                .post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateFranchiseRequest("Franquicia Demo"))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(FranchiseResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(franchise).isNotNull();
        assertThat(franchise.id()).isNotNull();
        Long franchiseId = franchise.id();

        BranchResponse branch = webTestClient
                .post()
                .uri("/api/v1/franchises/{fid}/branches", franchiseId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateBranchRequest("Sucursal Norte"))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(BranchResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(branch).isNotNull();
        Long branchId = branch.id();

        Long productLow = webTestClient
                .post()
                .uri("/api/v1/franchises/{fid}/branches/{bid}/products", franchiseId, branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateProductRequest("Producto A", 5))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ProductResponse.class)
                .returnResult()
                .getResponseBody()
                .id();

        Long productHigh = webTestClient
                .post()
                .uri("/api/v1/franchises/{fid}/branches/{bid}/products", franchiseId, branchId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateProductRequest("Producto B", 50))
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(ProductResponse.class)
                .returnResult()
                .getResponseBody()
                .id();

        webTestClient
                .patch()
                .uri(
                        "/api/v1/franchises/{fid}/branches/{bid}/products/{pid}/stock",
                        franchiseId,
                        branchId,
                        productLow)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateStockRequest(60))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.stock")
                .isEqualTo(60);

        List<MaxStockProductResponse> report = webTestClient
                .get()
                .uri("/api/v1/franchises/{fid}/reports/max-stock-by-branch", franchiseId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MaxStockProductResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(report).hasSize(1);
        assertThat(report.getFirst().stock()).isEqualTo(60);
        assertThat(report.getFirst().productId()).isEqualTo(productLow);

        webTestClient
                .delete()
                .uri(
                        "/api/v1/franchises/{fid}/branches/{bid}/products/{pid}",
                        franchiseId,
                        branchId,
                        productHigh)
                .exchange()
                .expectStatus()
                .isNoContent();

        webTestClient
                .delete()
                .uri(
                        "/api/v1/franchises/{fid}/branches/{bid}/products/{pid}",
                        franchiseId,
                        branchId,
                        productLow)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
