# Franchise Management API

API en **Java 21** y **Spring Boot 3** (stack **reactivo**: WebFlux) para el dominio de franquicias, sucursales y productos.

**Nota:** La integración con **Firebase** (Auth, Remote Config, etc.) corresponde sobre todo al cliente (Ionic); el backend se documenta HTTP, CORS y opción de validar tokens con Firebase Admin

- Endpoint reactivo: **`GET /api/v1/ping`**
- Actuator: **`GET /actuator/health`**
- Test de humo: `FranchiseManagementApplicationTest`

### Requisitos

- JDK 21  
- Maven 3.9+

### Comandos

```bash
mvn test
mvn spring-boot:run
# En otra terminal:
curl http://localhost:8080/api/v1/ping
curl http://localhost:8080/actuator/health
```

---

## Estructura de paquetes (objetivo final)

```
com.franchise.management
├── domain/           # entidades y reglas puras
├── application/      # casos de uso y puertos
├── infrastructure/ # R2DBC, Redis, adaptadores
└── presentation/     # rutas WebFlux, DTOs
```

