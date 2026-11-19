# Architecture

## Request flow

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

The dependency rule points inward: the controller and repositories depend on domain-owned port interfaces, while the domain does not depend on Spring or persistence details.

## Layers

### Controller

`infrastructure.adapter.input.rest.OperationController` is the REST adapter. It:

- exposes `/api/operations` endpoints;
- validates request DTOs with Jakarta Validation;
- converts `RegisterDeviceRequest` / `ReplaceDeviceRequest` into `SurveillanceDevice`;
- converts domain results into `ProcessResponse`, `DeviceResponse`, and `MessageResponse`.

### Input ports

Located in `domain.port.input`:

| Port | Purpose |
| --- | --- |
| `RegisterDeviceInformationUseCase` | Register a device. |
| `ReplaceDeviceInformationUseCase` | Replace a stored device. |
| `UpdateDeviceInformationUseCase` | Full or partial device update. |
| `DeleteDeviceInformationUseCase` | Delete a device by type. |
| `ProcessAllInformationUseCase` | Process stored or supplied information and expose the reconstructed message. |
| `GetStoredInformationUseCase` | Read stored devices. |

### Use cases

`application.usecase.DeviceInformationUseCase` implements `UpdateDeviceInformationUseCase` and `ProcessAllInformationUseCase` by delegating to `OperationDomainService`.

### Domain services

`domain.service.OperationDomainService` contains the core rules:

- duplicate-check before registration;
- partial-update validation;
- validation that exactly the three reference devices are present;
- trilateration using the fixed coordinates in `DeviceType`;
- finite-result and residual checks for calculated locations;
- reconstruction of a message from ordered fragments, including conflict detection.

### Output ports

Located in `domain.port.output`:

- `DeviceRepositoryPort`: save, update, replace, delete, find, and existence checks for devices.
- `OperationOutputPort`: save and find operations.

### Repositories

`infrastructure.adapter.output.memory.InMemoryDeviceRepository` stores devices in a `ConcurrentHashMap` keyed by `DeviceType` and returns defensive copies.

`InMemoryOperationRepository` does the same for `Operation` keyed by id.

## Concrete flows

### Register a device

```text
POST /api/operations/devices
  → OperationController
  → RegisterDeviceInformationUseCase
  → RegisterDeviceInformationUseCaseImpl
  → OperationDomainService.register
  → DeviceRepositoryPort
  → InMemoryDeviceRepository
```

### Process stored information

```text
GET /api/operations/process
  → OperationController
  → ProcessAllInformationUseCase
  → DeviceInformationUseCase
  → OperationDomainService.processAll / message
  → DeviceRepositoryPort.findAll
  → InMemoryDeviceRepository
```

The domain service indexes devices by type, requires `STAR_LABS`, `CCPD`, and `SATELLITE`, then solves the trilateration equations. `message()` independently merges fragments position by position.

### Process supplied information

```text
POST /api/operations/process
  → OperationController (DTO → SurveillanceDevice)
  → ProcessAllInformationUseCase
  → DeviceInformationUseCase
  → OperationDomainService.process / message
```

The supplied list is used directly; no repository read is needed for the calculation.

## Wiring

`infrastructure.configuration.BeanConfiguration` declares the repository, domain service, and input-port beans. All device input ports now resolve to application use cases: register/replace/delete/get-stored have dedicated implementations, while update/process are provided by `DeviceInformationUseCase`.
