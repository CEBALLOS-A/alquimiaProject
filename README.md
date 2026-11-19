# Alchemy Operation

Spring Boot 3.5 / Java 21 scaffold for registering surveillance devices, calculating a laboratory location by trilateration, and reconstructing a message from device fragments. The codebase follows a domain-driven hexagonal structure.

## Architecture

```text
Controller
    ↓
Input Ports
    ↓
Use Cases
    ↓
Domain Services
    ↓
Output Ports
    ↓
Repositories
```

| Layer | Location | Responsibility |
| --- | --- | --- |
| Controller | `src/main/java/com/alchemy/operation/infrastructure/adapter/input/rest` | HTTP adapter; maps DTOs to domain objects and back. |
| Input Ports | `src/main/java/com/alchemy/operation/domain/port/input` | Use-case contracts exposed to the outside world. |
| Use Cases | `src/main/java/com/alchemy/operation/application/usecase` | Application orchestration around the domain service. |
| Domain Services | `src/main/java/com/alchemy/operation/domain/service` | Business rules, validation, trilateration, message reconstruction. |
| Output Ports | `src/main/java/com/alchemy/operation/domain/port/output` | Persistence contracts owned by the domain. |
| Repositories | `src/main/java/com/alchemy/operation/infrastructure/adapter/output` | Concrete adapters; currently in-memory implementations. |

Wiring is defined in `infrastructure/configuration/BeanConfiguration.java`. All device input ports flow through application use cases before reaching `OperationDomainService`; update/process use `DeviceInformationUseCase`, while register/replace/delete/get-stored have dedicated implementations.

See [docs/architecture.md](docs/architecture.md) for the detailed request flows.

## Domain model

| Type | Role |
| --- | --- |
| `SurveillanceDevice` | Device type, measured distance, and ordered message fragments. |
| `DeviceType` | Reference device enum with fixed coordinates. |
| `LaboratoryLocation` | Wraps the calculated `Position`. |
| `Position` | Immutable-by-convention x/y coordinate pair. |
| `Operation` | Placeholder aggregate with `id` and `name`. |

See [docs/domain-model.md](docs/domain-model.md) for fields, invariants, and behavior.

## REST API

Base path: `/api/operations`

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/process` | Calculate location and message from the supplied devices. |
| `GET` | `/process` | Calculate location and message from stored devices. |
| `POST` | `/devices` | Register a device. |
| `PUT` | `/devices` | Replace a stored device. |
| `PATCH` | `/devices/{deviceType}` | Partially update distance and/or fragments. |
| `DELETE` | `/devices/{deviceType}` | Delete a device. |

## Build and test

```bash
mvn test
mvn spring-boot:run
```

## Documentation

- [Architecture](docs/architecture.md)
- [Domain model](docs/domain-model.md)
